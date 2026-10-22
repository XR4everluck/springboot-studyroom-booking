package com.yourname.studyroom.model.entity;

import com.yourname.studyroom.model.enums.ReleaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "座位信息表")
@TableName(value = "seat")
@Data
public class Seat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位编号")
    @TableField(value = "seat_code")
    private String seatCode;

    @Schema(description = "价格（元/小时）")
    @TableField(value = "price")
    private BigDecimal price;

    @Schema(description = "所属自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "座位类型：普通座/靠窗座/包间/沉浸式卡座")
    @TableField(value = "seat_type")
    private String seatType;

    @Schema(description = "是否有插座")
    @TableField(value = "has_power")
    private Boolean hasPower;

    @Schema(description = "是否有台灯")
    @TableField(value = "has_light")
    private Boolean hasLight;

    @Schema(description = "是否静音区")
    @TableField(value = "is_quiet")
    private Boolean isQuiet;

    @Schema(description = "是否发布")
    @TableField(value = "is_release")
    private ReleaseStatus isRelease;

}
