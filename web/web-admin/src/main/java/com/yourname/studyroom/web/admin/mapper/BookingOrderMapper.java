package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementQueryVo;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.BookingOrder
*/
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    IPage<AgreementVo> pageAgreementByQuery(IPage<AgreementVo> page, AgreementQueryVo queryVo);
}




