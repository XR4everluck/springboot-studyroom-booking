package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @TableName booking_term
 */
@TableName(value = "booking_term")
@Data
@Schema(description = "预订时长信息")
public class BookingTerm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "时长月数")
    @TableField("month_count")
    private Integer monthCount;

    @Schema(description = "时长单位:月")
    @TableField("unit")
    private String unit;
}