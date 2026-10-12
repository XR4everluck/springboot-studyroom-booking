package com.yourname.studyroom.web.admin.vo.agreement;

import com.yourname.studyroom.model.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租约信息")
public class AgreementVo extends BookingOrder {

    @Schema(description = "签约自习室门店信息")
    private StudyStore apartmentInfo;

    @Schema(description = "签约房间信息")
    private Seat roomInfo;

    @Schema(description = "支付方式")
    private PaymentType paymentType;

    @Schema(description = "租期")
    private LeaseTerm leaseTerm;
}
