package com.yourname.studyroom.web.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.CardRechargeRecord;
import com.yourname.studyroom.model.entity.MemberCard;

import java.util.List;

/**
 * 会员卡 Service
 */
public interface MemberCardService extends IService<MemberCard> {

    /**
     * 查询某用户可用（正常且未过期）的会员卡
     *
     * @param userId 用户id
     * @return 会员卡列表
     */
    List<MemberCard> listUsableByUserId(Long userId);

    /**
     * 为一次预订扣减会员卡额度，额度不足或卡不可用时抛出异常
     *
     * @param memberCardId 会员卡id
     * @param bookingOrder 预订订单（用于计算扣减额度）
     */
    void deductForBooking(Long memberCardId, BookingOrder bookingOrder);

    /**
     * 会员卡充值并记录流水：新增或更新会员卡，并累加剩余额度
     *
     * @param memberCard 会员卡（新增时 id 为空）
     * @param record     充值记录（含充值额度与金额）
     */
    void recharge(MemberCard memberCard, CardRechargeRecord record);
}
