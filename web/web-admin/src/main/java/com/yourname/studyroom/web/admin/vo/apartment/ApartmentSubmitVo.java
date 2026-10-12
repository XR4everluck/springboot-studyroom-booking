package com.yourname.studyroom.web.admin.vo.apartment;


import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "自习室门店信息")
@Data
public class ApartmentSubmitVo extends StudyStore {

    @Schema(description="自习室门店配套id")
    private List<Long> facilityInfoIds;

    @Schema(description="自习室门店标签id")
    private List<Long> labelIds;

    @Schema(description="自习室门店杂费值id")
    private List<Long> feeValueIds;

    @Schema(description="自习室门店图片id")
    private List<GraphVo> graphVoList;

}
