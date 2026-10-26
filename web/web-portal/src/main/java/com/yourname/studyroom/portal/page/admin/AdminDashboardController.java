package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.yourname.studyroom.web.admin.service.MemberCardService;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.yourname.studyroom.web.admin.vo.statistics.HomeStatisticsVo;
import com.yourname.studyroom.common.constant.RedisConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
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
 * 管理端首页数据看板
 */
@Controller
public class AdminDashboardController {

    private final BookingOrderService bookingOrderService;
    private final VisitAppointmentService visitAppointmentService;
    private final MemberCardService memberCardService;
    private final StringRedisTemplate redisTemplate;

    public AdminDashboardController(BookingOrderService bookingOrderService,
                                    VisitAppointmentService visitAppointmentService,
                                    MemberCardService memberCardService,
                                    StringRedisTemplate redisTemplate) {
        this.bookingOrderService = bookingOrderService;
        this.visitAppointmentService = visitAppointmentService;
        this.memberCardService = memberCardService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/admin/index")
    public String index(Model model) {
        LocalDate today = LocalDate.now();
        Date todayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowStart = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date weekStart = Date.from(today.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 今日新增订单
        model.addAttribute("todayOrders", bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart)));

        // 今日营收（不含已取消）
        List<BookingOrder> todayOrderList = bookingOrderService.list(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreateTime, todayStart)
                .lt(BookingOrder::getCreateTime, tomorrowStart));
        model.addAttribute("todayRevenue", sumRevenue(todayOrderList));

        // 今日到店体验预约
        model.addAttribute("todayAppointments", visitAppointmentService.count(new LambdaQueryWrapper<VisitAppointment>()
                .ge(VisitAppointment::getAppointmentTime, todayStart)
                .lt(VisitAppointment::getAppointmentTime, tomorrowStart)));

        // 有效会员卡
        model.addAttribute("memberCardCount", memberCardService.count(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getStatus, 1)));

        // 待处理订单（待确认/签退待确认/续期待确认）
        long pendingOrders = bookingOrderService.count(new LambdaQueryWrapper<BookingOrder>()
                .in(BookingOrder::getStatus, BookingStatus.CONFIRM_PENDING,
                        BookingStatus.CHECKOUT_PENDING, BookingStatus.RENEW_PENDING));
        model.addAttribute("pendingOrders", pendingOrders);

        // 订单状态分布（按状态分组计数，柱条可点击下钻到对应筛选的订单列表）
        List<Map<String, Object>> statusRows = bookingOrderService.listMaps(new QueryWrapper<BookingOrder>()
                .select("status", "count(*) as cnt")
                .eq("is_deleted", 0)
                .groupBy("status"));
        long maxCount = 1;
        long totalOrders = 0;
        Map<Integer, Long> countByCode = new java.util.LinkedHashMap<>();
        for (Map<String, Object> row : statusRows) {
            Integer code = ((Number) row.get("status")).intValue();
            long cnt = ((Number) row.get("cnt")).longValue();
            countByCode.merge(code, cnt, Long::sum);
            totalOrders += cnt;
            maxCount = Math.max(maxCount, cnt);
        }
        List<StatusCount> distribution = new ArrayList<>();
        for (BookingStatus st : BookingStatus.values()) {
            StatusCount sc = new StatusCount();
            sc.status = st;
            sc.count = countByCode.getOrDefault(st.getCode(), 0L);
            sc.percent = Math.round(sc.count * 1000.0 / maxCount) / 10.0;
            distribution.add(sc);
        }
        model.addAttribute("distribution", distribution);
        model.addAttribute("totalOrders", totalOrders);

        // 最近管理员登录（Redis 流水，时间|账号|结果|IP）
        List<String> loginLogs = redisTemplate.opsForList()
                .range(RedisConstant.ADMIN_LOGIN_LOG_PREFIX + "recent", 0, 4);
        List<Map<String, String>> recentLogins = new ArrayList<>();
        if (loginLogs != null) {
            for (String line : loginLogs) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4) {
                    Map<String, String> item = new java.util.LinkedHashMap<>();
                    item.put("time", parts[0]);
                    item.put("username", parts[1]);
                    item.put("result", parts[2]);
                    item.put("ip", parts[3]);
                    recentLogins.add(item);
                }
            }
        }
        model.addAttribute("recentLogins", recentLogins);

        // 近 7 日趋势（纯 CSS 柱状图渲染）
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
        model.addAttribute("weekTrend", trend);
        return "admin/index";
    }

    private BigDecimal sumRevenue(List<BookingOrder> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != BookingStatus.CANCELED && o.getTotalAmount() != null)
                .map(BookingOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 订单状态分布行（看板横条图数据） */
    public static class StatusCount {
        private BookingStatus status;
        private long count;
        private double percent;

        public BookingStatus getStatus() {
            return status;
        }

        public long getCount() {
            return count;
        }

        public double getPercent() {
            return percent;
        }
    }
}
