package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "自习室门店标签关联表")
@TableName(value = "store_label")
@Data
//@Builder
public class StoreLabel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "标签id")
    @TableField(value = "label_id")
    private Long labelId;

}