package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementQueryVo;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface BookingOrderService extends IService<BookingOrder> {

    AgreementVo getAgreementById(Long id);

    IPage<AgreementVo> pageAgreementByQuery(IPage<AgreementVo> page, AgreementQueryVo queryVo);

    /**
     * 创建或更新预订订单，含营业时间校验与时段冲突校验
     *
     * @param bookingOrder 预订订单
     */
    void createOrUpdateOrder(BookingOrder bookingOrder);

    /**
     * 签到：已预订 → 使用中，记录签到时间
     *
     * @param id 订单id
     */
    void checkIn(Long id);

    /**
     * 签退：使用中 → 已完成，记录签退时间
     *
     * @param id 订单id
     */
    void checkOut(Long id);
}
