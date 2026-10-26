package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.mapper.ReportMapper;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.yourname.studyroom.web.admin.service.MemberCardService;
import com.yourname.studyroom.web.admin.service.UserInfoService;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.yourname.studyroom.web.admin.vo.store.StoreItemVo;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 演示大屏：全屏暗色页，投屏展示核心经营指标（数据与报表中心同源）
 */
@Controller
public class AdminBoardController {

    private final BookingOrderService bookingOrderService;
    private final VisitAppointmentService visitAppointmentService;
    private final MemberCardService memberCardService;
    private final UserInfoService userInfoService;
    private final ReportMapper reportMapper;
    private final com.yourname.studyroom.web.app.service.StudyStoreService appStudyStoreService;

    public AdminBoardController(BookingOrderService bookingOrderService,
                                VisitAppointmentService visitAppointmentService,
                                MemberCardService memberCardService,
                                UserInfoService userInfoService,
                                ReportMapper reportMapper,
                                com.yourname.studyroom.web.app.service.StudyStoreService appStudyStoreService) {
        this.bookingOrderService = bookingOrderService;
        this.visitAppointmentService = visitAppointmentService;
        this.memberCardService = memberCardService;
        this.userInfoService = userInfoService;
        this.reportMapper = reportMapper;
        this.appStudyStoreService = appStudyStoreService;
    }

    @GetMapping("/admin/board")
    public String board(Model model) {
        LocalDate today = LocalDate.now();
        Date todayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowStart = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date weekStart = Date.from(today.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant());

        model.addAttribute("todayRevenue", sumRevenue(bookingOrderService.list(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart))));
        model.addAttribute("todayOrders", bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart)));
        model.addAttribute("inUseCount", bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .eq(BookingOrder::getStatus, BookingStatus.IN_USE)));
        model.addAttribute("todayAppointments", visitAppointmentService.count(new LambdaQueryWrapper<VisitAppointment>()
                .ge(VisitAppointment::getAppointmentTime, todayStart)
                .lt(VisitAppointment::getAppointmentTime, tomorrowStart)));
        model.addAttribute("totalRevenue", sumRevenue(bookingOrderService.list()));
        model.addAttribute("totalOrders", bookingOrderService.count());
        model.addAttribute("memberCount", memberCardService.count(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getStatus, 1)));
        model.addAttribute("userCount", userInfoService.count());

        // 近 7 日趋势
        List<BookingOrder> weekOrders = bookingOrderService.list(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, weekStart));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        List<Map<String, Object>> trend = new ArrayList<>();
        long maxDayOrders = 1;
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
            long cnt = dayOrders.size();
            maxDayOrders = Math.max(maxDayOrders, cnt);
            Map<String, Object> point = new java.util.LinkedHashMap<>();
            point.put("date", day.format(fmt));
            point.put("orders", cnt);
            point.put("revenue", sumRevenue(dayOrders));
            trend.add(point);
        }
        for (Map<String, Object> point : trend) {
            long cnt = ((Number) point.get("orders")).longValue();
            point.put("percent", Math.round(cnt * 100.0 / maxDayOrders));
        }
        model.addAttribute("weekTrend", trend);

        // 热门门店（Redis 缓存）
        List<com.yourname.studyroom.web.app.vo.store.StoreItemVo> hotStores =
                appStudyStoreService.getHotList(5);
        model.addAttribute("hotStores", hotStores);
        return "admin/board";
    }

    private BigDecimal sumRevenue(List<BookingOrder> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != BookingStatus.CANCELED && o.getTotalAmount() != null)
                .map(BookingOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
