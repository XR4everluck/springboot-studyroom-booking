package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.BusinessHoursUtil;
import com.yourname.studyroom.common.utils.OrderNoUtil;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.model.enums.SeatRealtimeStatus;
import com.yourname.studyroom.web.admin.mapper.*;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.order.OrderQueryVo;
import com.yourname.studyroom.web.admin.vo.order.OrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author liubo
 * @description 针对表【booking_order(预订订单信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class BookingOrderServiceImpl extends ServiceImpl<BookingOrderMapper, BookingOrder>
        implements BookingOrderService {

    @Autowired
    BookingOrderMapper bookingOrderMapper;

    @Autowired
    StudyStoreMapper studyStoreMapper;

    @Autowired
    SeatMapper seatMapper;

    @Autowired
    PaymentTypeMapper paymentTypeMapper;

    @Autowired
    BookingTermMapper bookingTermMapper;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public OrderVo getOrderById(Long id) {
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        StudyStore studyStore = studyStoreMapper.selectById(bookingOrder.getStoreId());
        Seat seat = seatMapper.selectById(bookingOrder.getSeatId());
        PaymentType paymentType = paymentTypeMapper.selectById(bookingOrder.getPaymentTypeId());
        BookingTerm bookingTerm = bookingTermMapper.selectById(bookingOrder.getBookingTermId());

        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(bookingOrder,orderVo);
        orderVo.setStudyStore(studyStore);
        orderVo.setSeat(seat);
        orderVo.setPaymentType(paymentType);
        orderVo.setBookingTerm(bookingTerm);

        return orderVo;
    }

    @Override
    public IPage<OrderVo> pageOrderByQuery(IPage<OrderVo> page, OrderQueryVo queryVo) {
        return bookingOrderMapper.pageOrderByQuery(page, queryVo);
    }

    /**
     * 创建或更新预订订单：
     * 1.校验预订起止时间是否落在门店营业时间内；
     * 2.校验同一座位同一时段是否已被有效预订占用；
     * 3.落库并刷新座位实时状态缓存。
     */
    @Override
    public void createOrUpdateOrder(BookingOrder bookingOrder) {
        StudyStore studyStore = studyStoreMapper.selectById(bookingOrder.getStoreId());
        if (studyStore == null) {
            throw new BookingException(ResultCodeEnum.DATA_ERROR);
        }
        boolean withinBusinessHours = BusinessHoursUtil.isRangeWithin(
                studyStore.getBusinessHours(),
                bookingOrder.getStartDate(),
                bookingOrder.getEndDate());
        if (!withinBusinessHours) {
            throw new BookingException(ResultCodeEnum.BOOKING_NOT_IN_BUSINESS_HOURS);
        }

        int conflict = bookingOrderMapper.countTimeConflict(
                bookingOrder.getSeatId(),
                bookingOrder.getStartDate(),
                bookingOrder.getEndDate(),
                bookingOrder.getId());
        if (conflict > 0) {
            throw new BookingException(ResultCodeEnum.SEAT_TIME_CONFLICT);
        }

        if (bookingOrder.getStatus() == null) {
            bookingOrder.setStatus(BookingStatus.BOOKED);
        }
        if (bookingOrder.getId() == null && bookingOrder.getOrderNo() == null) {
            bookingOrder.setOrderNo(OrderNoUtil.generate());
        }
        this.saveOrUpdate(bookingOrder);

        updateSeatRealtimeStatus(bookingOrder.getSeatId(), toSeatRealtimeStatus(bookingOrder.getStatus()));
    }

    /**
     * 签到：仅"已预订"可签到，状态流转为"使用中"并记录签到时间
     */
    @Override
    public void checkIn(Long id) {
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        if (bookingOrder == null) {
            throw new BookingException(ResultCodeEnum.BOOKING_ORDER_NOT_EXIST);
        }
        if (bookingOrder.getStatus() != BookingStatus.BOOKED) {
            throw new BookingException(ResultCodeEnum.BOOKING_STATUS_NOT_ALLOW_CHECK_IN);
        }
        bookingOrder.setStatus(BookingStatus.IN_USE);
        bookingOrder.setCheckInTime(new Date());
        this.updateById(bookingOrder);

        updateSeatRealtimeStatus(bookingOrder.getSeatId(), SeatRealtimeStatus.IN_USE);
    }

    /**
     * 签退：仅"使用中"可签退，状态流转为"已完成"并记录签退时间，释放座位
     */
    @Override
    public void checkOut(Long id) {
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        if (bookingOrder == null) {
            throw new BookingException(ResultCodeEnum.BOOKING_ORDER_NOT_EXIST);
        }
        if (bookingOrder.getStatus() != BookingStatus.IN_USE) {
            throw new BookingException(ResultCodeEnum.BOOKING_STATUS_NOT_ALLOW_CHECK_OUT);
        }
        bookingOrder.setStatus(BookingStatus.COMPLETED);
        bookingOrder.setCheckOutTime(new Date());
        this.updateById(bookingOrder);

        updateSeatRealtimeStatus(bookingOrder.getSeatId(), SeatRealtimeStatus.FREE);
    }

    private SeatRealtimeStatus toSeatRealtimeStatus(BookingStatus status) {
        if (status == BookingStatus.IN_USE) {
            return SeatRealtimeStatus.IN_USE;
        }
        if (status == BookingStatus.BOOKED || status == BookingStatus.CONFIRM_PENDING
                || status == BookingStatus.CHECKOUT_PENDING || status == BookingStatus.RENEW_PENDING) {
            return SeatRealtimeStatus.BOOKED;
        }
        return SeatRealtimeStatus.FREE;
    }

    /**
     * 管理端订单状态确认：更新状态并同步座位实时状态缓存（确认签退后座位释放为空闲）
     */
    @Override
    public void confirmStatus(Long id, BookingStatus target) {
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        if (bookingOrder == null) {
            throw new BookingException(ResultCodeEnum.BOOKING_ORDER_NOT_EXIST);
        }
        bookingOrder.setStatus(target);
        this.updateById(bookingOrder);
        updateSeatRealtimeStatus(bookingOrder.getSeatId(), toSeatRealtimeStatus(target));
    }

    private void updateSeatRealtimeStatus(Long seatId, SeatRealtimeStatus status) {
        String key = RedisConstant.APP_SEAT_STATUS_PREFIX + seatId;
        redisTemplate.opsForValue().set(key, status.getCode(), RedisConstant.APP_SEAT_STATUS_TTL_SEC, TimeUnit.SECONDS);
    }
}




