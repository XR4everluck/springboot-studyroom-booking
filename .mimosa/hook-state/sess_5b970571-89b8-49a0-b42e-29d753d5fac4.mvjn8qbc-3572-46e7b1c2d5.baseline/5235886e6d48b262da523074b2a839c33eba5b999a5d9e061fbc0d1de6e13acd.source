package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yourname.studyroom.model.enums.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "会员卡充值记录表")
@TableName(value = "card_recharge_record")
@Data
public class CardRechargeRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会员卡id")
    @TableField(value = "member_card_id")
    private Long memberCardId;

    @Schema(description = "用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "充值对应卡类型")
    @TableField(value = "recharge_type")
    private CardType rechargeType;

    @Schema(description = "本次充值额度")
    @TableField(value = "recharge_quantity")
    private Integer rechargeQuantity;

    @Schema(description = "本次充值金额（元）")
    @TableField(value = "amount")
    private BigDecimal amount;

    @Schema(description = "备注信息")
    @TableField(value = "additional_info")
    private String additionalInfo;

}
