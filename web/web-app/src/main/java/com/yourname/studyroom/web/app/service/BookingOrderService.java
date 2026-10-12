package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.agreement.AgreementDetailVo;
import com.yourname.studyroom.web.app.vo.agreement.AgreementItemVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface BookingOrderService extends IService<BookingOrder> {
    List<AgreementItemVo> listItemByPhone(String phone);

    AgreementDetailVo getDetailById(Long id);
}
