package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.web.admin.mapper.*;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementQueryVo;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    LeaseTermMapper leaseTermMapper;

    @Override
    public AgreementVo getAgreementById(Long id) {
        BookingOrder bookingOrder = bookingOrderMapper.selectById(id);
        StudyStore studyStore = studyStoreMapper.selectById(bookingOrder.getStoreId());
        Seat seat = seatMapper.selectById(bookingOrder.getSeatId());
        PaymentType paymentType = paymentTypeMapper.selectById(bookingOrder.getPaymentTypeId());
        LeaseTerm leaseTerm = leaseTermMapper.selectById(bookingOrder.getLeaseTermId());

        AgreementVo agreementVo = new AgreementVo();
        BeanUtils.copyProperties(bookingOrder,agreementVo);
        agreementVo.setApartmentInfo(studyStore);
        agreementVo.setRoomInfo(seat);
        agreementVo.setPaymentType(paymentType);
        agreementVo.setLeaseTerm(leaseTerm);

        return agreementVo;
    }

    @Override
    public IPage<AgreementVo> pageAgreementByQuery(IPage<AgreementVo> page, AgreementQueryVo queryVo) {
        return bookingOrderMapper.pageAgreementByQuery(page, queryVo);
    }
}




