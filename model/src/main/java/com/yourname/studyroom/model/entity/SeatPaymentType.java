package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "座位&支付方式关联表")
@TableName(value = "seat_payment_type")
@Data
@Builder
public class SeatPaymentType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位id")
    @TableField(value = "seat_id")
    private Long seatId;

    @Schema(description = "支付类型id")
    @TableField(value = "payment_type_id")
    private Long paymentTypeId;


}