package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.CardRechargeRecord;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.model.enums.CardType;
import com.yourname.studyroom.web.app.mapper.CardRechargeRecordMapper;
import com.yourname.studyroom.web.app.mapper.AppMemberCardMapper;
import com.yourname.studyroom.web.app.service.MemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 会员卡 Service 实现
 */
@Service
public class MemberCardServiceImpl extends ServiceImpl<AppMemberCardMapper, MemberCard>
        implements MemberCardService {

    private static final long ONE_DAY_MILLIS = 24L * 60 * 60 * 1000;

    @Autowired
    AppMemberCardMapper memberCardMapper;

    @Autowired
    CardRechargeRecordMapper cardRechargeRecordMapper;

    @Override
    public List<MemberCard> listUsableByUserId(Long userId) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getUserId, userId);
        wrapper.eq(MemberCard::getStatus, 1);
        wrapper.and(w -> w.isNull(MemberCard::getValidUntil).or().ge(MemberCard::getValidUntil, new Date()));
        wrapper.orderByDesc(MemberCard::getId);
        return memberCardMapper.selectList(wrapper);
    }

    /**
     * 为一次预订扣减会员卡额度：
     * 次卡每次扣 1；小时卡按预订小时数扣；月卡按预订天数扣。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductForBooking(Long memberCardId, BookingOrder bookingOrder) {
        MemberCard card = memberCardMapper.selectById(memberCardId);
        if (card == null || card.getStatus() == null || card.getStatus() != 1) {
            throw new BookingException(ResultCodeEnum.MEMBER_CARD_NOT_ENOUGH);
        }
        // 门店限定卡需与订单门店一致
        if (card.getStoreId() != null && !card.getStoreId().equals(bookingOrder.getStoreId())) {
            throw new BookingException(ResultCodeEnum.MEMBER_CARD_NOT_ENOUGH);
        }
        // 有效期校验
        if (card.getValidUntil() != null && card.getValidUntil().before(new Date())) {
            throw new BookingException(ResultCodeEnum.MEMBER_CARD_NOT_ENOUGH);
        }

        int required = requiredQuantity(card.getCardType(), bookingOrder);
        int remaining = card.getRemainingQuantity() == null ? 0 : card.getRemainingQuantity();
        if (remaining < required) {
            throw new BookingException(ResultCodeEnum.MEMBER_CARD_NOT_ENOUGH);
        }
        card.setRemainingQuantity(remaining - required);
        memberCardMapper.updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(MemberCard memberCard, CardRechargeRecord record) {
        this.saveOrUpdate(memberCard);
        if (record != null) {
            record.setMemberCardId(memberCard.getId());
            record.setUserId(memberCard.getUserId());
            record.setRechargeType(memberCard.getCardType());
            int remaining = memberCard.getRemainingQuantity() == null ? 0 : memberCard.getRemainingQuantity();
            int rechargeQty = record.getRechargeQuantity() == null ? 0 : record.getRechargeQuantity();
            memberCard.setRemainingQuantity(remaining + rechargeQty);
            int total = memberCard.getTotalQuantity() == null ? 0 : memberCard.getTotalQuantity();
            memberCard.setTotalQuantity(total + rechargeQty);
            this.updateById(memberCard);
            cardRechargeRecordMapper.insert(record);
        }
    }

    /**
     * 根据卡类型与预订起止时间计算需要扣减的额度
     */
    private int requiredQuantity(CardType cardType, BookingOrder bookingOrder) {
        if (cardType == null) {
            return 1;
        }
        long days = durationDays(bookingOrder);
        switch (cardType) {
            case HOURS:
                return (int) (days * 24);
            case MONTH:
                return (int) days;
            case TIMES:
            default:
                return 1;
        }
    }

    private long durationDays(BookingOrder bookingOrder) {
        Date start = bookingOrder.getStartDate();
        Date end = bookingOrder.getEndDate();
        if (start == null || end == null || end.before(start)) {
            return 1;
        }
        long diff = end.getTime() - start.getTime();
        return Math.max(1, (long) Math.ceil(diff / (double) ONE_DAY_MILLIS));
    }
}
