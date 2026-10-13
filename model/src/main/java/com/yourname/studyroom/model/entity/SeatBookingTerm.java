package com.yourname.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName seat_booking_term
 */
@TableName(value = "seat_booking_term")
@Data
@Schema(description = "座位预订时长关系表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatBookingTerm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位id")
    @TableField("seat_id")
    private Long seatId;

    @Schema(description = "预订时长id")
    @TableField("booking_term_id")
    private Long bookingTermId;

}