package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "座位&基本属性值关联表")
@TableName(value = "seat_attr_value")
@Data
@Builder
public class SeatAttrValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位id")
    @TableField(value = "seat_id")
    private Long seatId;

    @Schema(description = "属性值id")
    @TableField(value = "attr_value_id")
    private Long attrValueId;
}