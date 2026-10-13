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

    /**
     * 创建或更新预订订单，含营业时间校验与时段冲突校验，成功后刷新座位实时状态缓存
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
