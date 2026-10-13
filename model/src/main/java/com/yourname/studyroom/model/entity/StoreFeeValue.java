package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "自习室门店&杂费关联表")
@TableName(value = "store_fee_value")
@Data
//@Builder
public class StoreFeeValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "收费项value_id")
    @TableField(value = "fee_value_id")
    private Long feeValueId;

}