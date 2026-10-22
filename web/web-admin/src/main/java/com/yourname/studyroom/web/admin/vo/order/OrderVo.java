package com.yourname.studyroom.web.admin.vo.order;

import com.yourname.studyroom.model.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预订订单信息")
public class OrderVo extends BookingOrder {

    @Schema(description = "所属自习室门店信息")
    private StudyStore studyStore;

    @Schema(description = "预订座位信息")
    private Seat seat;

    @Schema(description = "支付方式")
    private PaymentType paymentType;

    @Schema(description = "预订时长")
    private BookingTerm bookingTerm;
}
