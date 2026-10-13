package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "自习室门店&配套关系")
@TableName(value = "store_facility")
@Data
//@Builder
public class StoreFacility extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "设施id")
    @TableField(value = "facility_id")
    private Long facilityId;


}