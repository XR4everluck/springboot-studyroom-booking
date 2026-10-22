package com.yourname.studyroom.web.admin.controller.statistics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.yourname.studyroom.web.admin.service.MemberCardService;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.yourname.studyroom.web.admin.vo.statistics.HomeStatisticsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description 首页数据看板统计
 */
@Tag(name = "数据看板统计")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private VisitAppointmentService visitAppointmentService;

    @Autowired
    private MemberCardService memberCardService;

    /**
     * 首页看板：今日订单/营收/预约/会员卡 + 近 7 日趋势
     */
    @Operation(summary = "查询首页看板统计数据")
    @GetMapping("home")
    public Result<HomeStatisticsVo> home() {
        LocalDate today = LocalDate.now();
        Date todayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowStart = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date weekStart = Date.from(today.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant());

        HomeStatisticsVo vo = new HomeStatisticsVo();

        // 今日新增订单
        vo.setTodayOrders(bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart)));

        // 今日营收（不含已取消）
        List<BookingOrder> todayOrders = bookingOrderService.list(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart));
        vo.setTodayRevenue(sumRevenue(todayOrders));

        // 今日到店体验预约
        vo.setTodayAppointments(visitAppointmentService.count(new LambdaQueryWrapper<VisitAppointment>()
                .ge(VisitAppointment::getAppointmentTime, todayStart)
                .lt(VisitAppointment::getAppointmentTime, tomorrowStart)));

        // 有效会员卡
        vo.setMemberCardCount(memberCardService.count(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getStatus, 1)));

        // 待处理订单（待确认/签退待确认/续期待确认）
        vo.setPendingOrders(bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .in(BookingOrder::getStatus, BookingStatus.CONFIRM_PENDING,
                        BookingStatus.CHECKOUT_PENDING, BookingStatus.RENEW_PENDING)));

        // 近 7 日趋势
        List<BookingOrder> weekOrders = bookingOrderService.list(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, weekStart));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        List<HomeStatisticsVo.TrendPoint> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Date dayStart = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date nextStart = Date.from(day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<BookingOrder> dayOrders = weekOrders.stream()
                    .filter(o -> {
                        Date c = o.getCreateTime();
                        return c != null && !c.before(dayStart) && c.before(nextStart);
                    })
                    .toList();
            HomeStatisticsVo.TrendPoint point = new HomeStatisticsVo.TrendPoint();
            point.setDate(day.format(fmt));
            point.setOrderCount((long) dayOrders.size());
            point.setRevenue(sumRevenue(dayOrders));
            trend.add(point);
        }
        vo.setWeekTrend(trend);
        return Result.ok(vo);
    }

    private BigDecimal sumRevenue(List<BookingOrder> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != BookingStatus.CANCELED && o.getTotalAmount() != null)
                .map(BookingOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
