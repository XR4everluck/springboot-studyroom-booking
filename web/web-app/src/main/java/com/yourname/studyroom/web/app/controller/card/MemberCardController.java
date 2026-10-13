package com.yourname.studyroom.web.app.controller.card;

import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.CardRechargeRecord;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.web.app.service.MemberCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "会员卡信息")
@RestController
@RequestMapping("/app/card")
public class MemberCardController {

    @Autowired
    MemberCardService memberCardService;

    /**
     * 获取当前用户可用会员卡列表
     */
    @Operation(summary = "获取我的可用会员卡")
    @GetMapping("listMine")
    public Result<List<MemberCard>> listMine() {
        List<MemberCard> result = memberCardService.listUsableByUserId(LoginUserHolder.getLoginUser().getUserId());
        return Result.ok(result);
    }

    /**
     * 会员卡充值/办卡：新增或累加会员卡额度并记录流水
     *
     * @param memberCard       会员卡（新卡 id 为空）
     * @param rechargeQuantity 本次充值额度
     * @param amount           本次充值金额
     */
    @Operation(summary = "会员卡充值/办卡")
    @PostMapping("recharge")
    public Result recharge(@RequestBody MemberCard memberCard,
                           @RequestParam Integer rechargeQuantity,
                           @RequestParam BigDecimal amount) {
        if (memberCard.getUserId() == null) {
            memberCard.setUserId(LoginUserHolder.getLoginUser().getUserId());
        }
        CardRechargeRecord record = new CardRechargeRecord();
        record.setRechargeQuantity(rechargeQuantity);
        record.setAmount(amount);
        memberCardService.recharge(memberCard, record);
        return Result.ok();
    }
}
