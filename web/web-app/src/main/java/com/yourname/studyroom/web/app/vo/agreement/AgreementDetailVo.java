package com.yourname.studyroom.web.app.vo.agreement;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "租约详细信息")
public class AgreementDetailVo extends BookingOrder {

    @Schema(description = "自习室门店名称")
    private String apartmentName;

    @Schema(description = "自习室门店图片列表")
    private List<GraphVo> apartmentGraphVoList;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "房间图片列表")
    private List<GraphVo> roomGraphVoList;

    @Schema(description = "支付方式")
    private String paymentTypeName;

    @Schema(description = "租期月数")
    private Integer leaseTermMonthCount;

    @Schema(description = "租期单位")
    private String leaseTermUnit;

}