package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.BusinessHoursUtil;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.model.enums.SeatRealtimeStatus;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.yourname.studyroom.web.app.service.MemberCardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.order.OrderDetailVo;
import com.yourname.studyroom.web.app.vo.order.OrderItemVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liubo
 * @description 针对表【booking_order(预订订单信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
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
    GraphInfoMapper graphInfoMapper;

    @Autowired
    PaymentTypeMapper paymentTypeMapper;

    @Autowired
    BookingTermMapper bookingTermMapper;

    @Autowired
    MemberCardService memberCardService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据手机号查询预订订单基本信息列表
     *
     * @param phone 手机号
     * @return 预订订单基本信息列表
     */
    @Override
    public List<OrderItemVo> listItemByPhone(String phone) {
        return bookingOrderMapper.listItemByPhone(phone);
    }

    /**
     * 根据ID获取预订订单详细信息
     *
     * @param id 预订订单ID
     * @return 预订订单详细信息对象
     */
    @Override
    public OrderDetailVo getDetailById(Long id) {
        //1.查询预订订单信息
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        if (bookingOrder == null) {
            return null;
        }
        //2.查询自习室门店信息
        StudyStore studyStore = studyStoreMapper.selectById(bookingOrder.getStoreId());

        //3.查询座位信息
        Seat seat = seatMapper.selectById(bookingOrder.getSeatId());

        //4.查询图片信息
        List<GraphVo> seatGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.SEAT, bookingOrder.getSeatId());
        List<GraphVo> storeGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.STORE, bookingOrder.getStoreId());

        //5.查询支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(bookingOrder.getPaymentTypeId());

        //6.查询预订时长
        BookingTerm bookingTerm = bookingTermMapper.selectById(bookingOrder.getBookingTermId());

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        BeanUtils.copyProperties(bookingOrder, orderDetailVo);
        orderDetailVo.setStoreName(studyStore.getName());
        orderDetailVo.setSeatCode(seat.getSeatCode());
        orderDetailVo.setStoreGraphVoList(storeGraphVoList);
        orderDetailVo.setSeatGraphVoList(seatGraphVoList);
        orderDetailVo.setPaymentTypeName(paymentType.getName());
        orderDetailVo.setBookingTermMonthCount(bookingTerm.getMonthCount());
        orderDetailVo.setBookingTermUnit(bookingTerm.getUnit());

        return orderDetailVo;
    }

    /**
     * 创建或更新预订订单：
     * 1.校验预订起止时间是否落在门店营业时间内；
     * 2.校验同一座位同一时段是否已被有效预订占用；
     * 3.落库并刷新座位实时状态缓存。
     */
    @Override
    public void createOrUpdateOrder(BookingOrder bookingOrder) {
        // 1.营业时间校验
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

        // 2.时段冲突校验
        int conflict = bookingOrderMapper.countTimeConflict(
                bookingOrder.getSeatId(),
                bookingOrder.getStartDate(),
                bookingOrder.getEndDate(),
                bookingOrder.getId());
        if (conflict > 0) {
            throw new BookingException(ResultCodeEnum.SEAT_TIME_CONFLICT);
        }

        // 3.新单默认状态
        if (bookingOrder.getStatus() == null) {
            bookingOrder.setStatus(BookingStatus.BOOKED);
        }

        // 3.1 新单使用会员卡抵扣时扣减额度
        boolean isNewOrder = bookingOrder.getId() == null;
        if (isNewOrder && bookingOrder.getMemberCardId() != null) {
            memberCardService.deductForBooking(bookingOrder.getMemberCardId(), bookingOrder);
        }

        this.saveOrUpdate(bookingOrder);

        // 4.刷新座位实时状态缓存
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

    /**
     * 订单状态 → 座位实时状态映射
     */
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
     * 写入座位实时状态缓存
     */
    private void updateSeatRealtimeStatus(Long seatId, SeatRealtimeStatus status) {
        String key = RedisConstant.APP_SEAT_STATUS_PREFIX + seatId;
        redisTemplate.opsForValue().set(key, status.getCode(), RedisConstant.APP_SEAT_STATUS_TTL_SEC, TimeUnit.SECONDS);
    }
}
