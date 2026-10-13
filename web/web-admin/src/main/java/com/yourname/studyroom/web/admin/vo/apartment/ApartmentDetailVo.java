package com.yourname.studyroom.web.admin.vo.apartment;


import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.SeatFacility;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import com.yourname.studyroom.web.admin.vo.fee.FeeValueVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "自习室门店信息")
@Data
public class ApartmentDetailVo extends StudyStore {

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "标签列表")
    private List<StoreLabel> storeLabelList;

    @Schema(description = "配套列表")
    private List<SeatFacility> seatFacilityList;

    @Schema(description = "杂费列表")
    private List<FeeValueVo> feeValueVoList;

}
