package com.yourname.studyroom.web.admin.vo.seat;

import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.web.admin.vo.attr.AttrValueVo;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "座位信息")
@Data
public class SeatDetailVo extends Seat {

    @Schema(description = "所属自习室门店信息")
    private StudyStore studyStore;

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "属性信息列表")
    private List<AttrValueVo> attrValueVoList;

    @Schema(description = "配套信息列表")
    private List<FacilityInfo> facilityInfoList;

    @Schema(description = "标签信息列表")
    private List<LabelInfo> labelInfoList;

    @Schema(description = "支付方式列表")
    private List<PaymentType> paymentTypeList;

    @Schema(description = "可选预订时长列表")
    private List<BookingTerm> bookingTermList;
}
