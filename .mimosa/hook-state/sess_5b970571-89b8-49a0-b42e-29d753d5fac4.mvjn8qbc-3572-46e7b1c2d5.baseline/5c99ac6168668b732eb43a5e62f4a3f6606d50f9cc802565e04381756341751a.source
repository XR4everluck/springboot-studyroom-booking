package com.yourname.studyroom.web.admin.schedule;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 定时任务类
 */
@Component
public class ScheduleTasks {

    @Autowired
    private BookingOrderService bookingOrderService;

    /**
     * 定时任务：检查预订订单状态
     * <p>
     * 使用cron表达式设置任务执行时间，每天午夜执行一次。
     * 结束日期早于今天的订单视为已到期；签退待确认的订单交由后台确认，不在这里改写状态。
     * ┌───────────── second (0-59)
     * │ ┌───────────── minute (0 - 59)
     * │ │ ┌───────────── hour (0 - 23)
     * │ │ │ ┌───────────── day of the month (1 - 31)
     * │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
     * │ │ │ │ │ ┌───────────── day of the week (0 - 7)
     * │ │ │ │ │ │          (0 or 7 is Sunday, or MON-SUN)
     * │ │ │ │ │ │
     * * * * * * *
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkBookingStatus() {
        // end_date 只精确到“天”，今天到期的订单当天仍应可用，因此只清理结束日期早于今天零点的订单
        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LambdaUpdateWrapper<BookingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.lt(BookingOrder::getEndDate, todayStart);
        updateWrapper.in(BookingOrder::getStatus, BookingStatus.BOOKED, BookingStatus.IN_USE);
        updateWrapper.set(BookingOrder::getStatus, BookingStatus.EXPIRED);
        bookingOrderService.update(updateWrapper);
    }
}
