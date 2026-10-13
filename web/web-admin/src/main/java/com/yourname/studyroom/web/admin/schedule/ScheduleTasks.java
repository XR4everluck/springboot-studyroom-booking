package com.yourname.studyroom.web.admin.schedule;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
     * 检查所有已确认或正在退座的预订订单，如果订单结束日期早于当前日期，则更新预订订单状态。
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
        LambdaUpdateWrapper<BookingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        Date now = new Date();
        updateWrapper.le(BookingOrder::getEndDate, now);
        updateWrapper.in(BookingOrder::getStatus, BookingStatus.BOOKED, BookingStatus.CHECKOUT_PENDING);
        updateWrapper.set(BookingOrder::getStatus, BookingStatus.EXPIRED);
        bookingOrderService.update(updateWrapper);
    }
}
