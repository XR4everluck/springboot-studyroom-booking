package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.SeatPaymentType;
import com.yourname.studyroom.web.app.service.SeatPaymentTypeService;
import com.yourname.studyroom.web.app.mapper.AppSeatPaymentTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【seat_payment_type(座位&支付方式关联表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class SeatPaymentTypeServiceImpl extends ServiceImpl<AppSeatPaymentTypeMapper, SeatPaymentType>
    implements SeatPaymentTypeService{

}




