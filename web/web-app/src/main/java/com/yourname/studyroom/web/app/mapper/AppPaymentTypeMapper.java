package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.PaymentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【payment_type(支付方式表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.PaymentType
*/
public interface AppPaymentTypeMapper extends BaseMapper<PaymentType> {

    List<PaymentType> selectListBySeatId(Long id);
}




