package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yourname.studyroom.model.enums.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "会员卡信息表")
@TableName(value = "member_card")
@Data
public class MemberCard extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会员卡号")
    @TableField(value = "card_no")
    private String cardNo;

    @Schema(description = "持卡用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "归属自习室门店id，null 表示通用卡")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "卡类型：次卡/小时卡/月卡")
    @TableField(value = "card_type")
    private CardType cardType;

    @Schema(description = "总额度")
    @TableField(value = "total_quantity")
    private Integer totalQuantity;

    @Schema(description = "剩余额度")
    @TableField(value = "remaining_quantity")
    private Integer remainingQuantity;

    @Schema(description = "有效期至")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "valid_until")
    private Date validUntil;

    @Schema(description = "状态 0停用 1正常")
    @TableField(value = "status")
    private Integer status;

}
