package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "座位&配套关联表")
@TableName(value = "seat_facility")
@Data
@Builder
public class SeatFacility extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位id")
    @TableField(value = "seat_id")
    private Long seatId;

    @Schema(description = "座位设施id")
    @TableField(value = "facility_id")
    private Long facilityId;

}