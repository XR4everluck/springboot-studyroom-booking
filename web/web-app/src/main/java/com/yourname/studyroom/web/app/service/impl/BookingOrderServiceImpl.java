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
    BookingOrderMapper leaseAgreementMapper;

    @Autowired
    StudyStoreMapper apartmentInfoMapper;

    @Autowired
    SeatMapper roomInfoMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;

    @Autowired
    PaymentTypeMapper paymentTypeMapper;

    @Autowired
    LeaseTermMapper leaseTermMapper;

    /**
     * 根据手机号查询租约基本信息列表
     *
     * @param phone 手机号
     * @return 租约基本信息列表
     */
    @Override
    public List<AgreementItemVo> listItemByPhone(String phone) {
        return leaseAgreementMapper.listItemByPhone(phone);
    }

    /**
     * 根据ID获取租约详细信息
     *
     * @param id 租约ID
     * @return 租约详细信息对象
     */
    @Override
    public AgreementDetailVo getDetailById(Long id) {
        //1.查询租约信息
        BookingOrder leaseAgreement = leaseAgreementMapper.selectById(id);
        if (leaseAgreement == null) {
            return null;
        }
        //2.查询自习室门店信息
        StudyStore apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getStoreId());

        //3.查询房间信息
        Seat roomInfo = roomInfoMapper.selectById(leaseAgreement.getSeatId());

        //4.查询图片信息
        List<GraphVo> roomGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, leaseAgreement.getSeatId());
        List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, leaseAgreement.getStoreId());

        //5.查询支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());

        //6.查询租期
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentInfo.getName());
        agreementDetailVo.setRoomNumber(roomInfo.getSeatCode());
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        agreementDetailVo.setPaymentTypeName(paymentType.getName());
        agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
        agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());

        return agreementDetailVo;
    }
}




