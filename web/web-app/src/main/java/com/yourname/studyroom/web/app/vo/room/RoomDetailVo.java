package com.yourname.studyroom.web.app.vo.room;

import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.app.vo.attr.AttrValueVo;
import com.yourname.studyroom.web.app.vo.fee.FeeValueVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "APP座位详情")
public class RoomDetailVo extends Seat {

    @Schema(description = "所属自习室门店信息")
    private ApartmentItemVo storeItemVo;

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "属性信息列表")
    private List<AttrValueVo> attrValueVoList;

    @Schema(description = "配套信息列表")
    private List<SeatFacility> seatFacilityList;

    @Schema(description = "标签信息列表")
    private List<StoreLabel> storeLabelList;

    @Schema(description = "支付方式列表")
    private List<PaymentType> paymentTypeList;

    @Schema(description = "杂费列表")
    private List<FeeValueVo> feeValueVoList;

    @Schema(description = "租期列表")
    private List<LeaseTerm> leaseTermList;

}
