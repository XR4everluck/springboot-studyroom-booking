package com.yourname.studyroom.web.app.vo.apartment;


import com.yourname.studyroom.model.entity.ApartmentInfo;
import com.yourname.studyroom.model.entity.LabelInfo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "App端自习室门店信息")
public class ApartmentItemVo extends ApartmentInfo {

    private List<LabelInfo> labelInfoList;

    private List<GraphVo> graphVoList;

    private BigDecimal minRent;
}
