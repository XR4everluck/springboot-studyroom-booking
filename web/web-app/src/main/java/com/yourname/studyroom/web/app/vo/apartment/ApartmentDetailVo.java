package com.yourname.studyroom.web.app.vo.apartment;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.SeatFacility;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@Schema(description = "APP端自习室门店信息详情")
public class ApartmentDetailVo extends StudyStore {

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "标签列表")
    private List<StoreLabel> labelInfoList;

    @Schema(description = "配套列表")
    private List<SeatFacility> facilityInfoList;

    @Schema(description = "租金最小值")
    private BigDecimal minRent;
}
