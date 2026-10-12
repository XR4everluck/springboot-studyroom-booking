package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.BookingOrder
*/
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    List<AgreementItemVo> listItemByPhone(String phone);
}




