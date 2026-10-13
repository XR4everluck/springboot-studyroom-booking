package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.agreement.AgreementDetailVo;
import com.yourname.studyroom.web.app.vo.agreement.AgreementItemVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    LeaseTermMapper leaseTermMapper;

    /**
     * 根据手机号查询预订订单基本信息列表
     *
     * @param phone 手机号
     * @return 预订订单基本信息列表
     */
    @Override
    public List<AgreementItemVo> listItemByPhone(String phone) {
        return bookingOrderMapper.listItemByPhone(phone);
    }

    /**
     * 根据ID获取预订订单详细信息
     *
     * @param id 预订订单ID
     * @return 预订订单详细信息对象
     */
    @Override
    public AgreementDetailVo getDetailById(Long id) {
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
        List<GraphVo> roomGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, bookingOrder.getSeatId());
        List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, bookingOrder.getStoreId());

        //5.查询支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(bookingOrder.getPaymentTypeId());

        //6.查询租期
        LeaseTerm leaseTerm = leaseTermMapper.selectById(bookingOrder.getLeaseTermId());

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(bookingOrder, agreementDetailVo);
        agreementDetailVo.setApartmentName(studyStore.getName());
        agreementDetailVo.setRoomNumber(seat.getSeatCode());
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        agreementDetailVo.setPaymentTypeName(paymentType.getName());
        agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
        agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());

        return agreementDetailVo;
    }
}




