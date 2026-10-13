package com.yourname.studyroom.web.app.vo.history;


import com.yourname.studyroom.model.entity.BrowsingHistory;
import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "浏览历史基本信息")
public class HistoryItemVo extends BrowsingHistory {

    @Schema(description = "座位号")
    private String seatCode;

    @Schema(description = "座位单价")
    private BigDecimal rent;

    @Schema(description = "座位图片列表")
    private List<GraphVo> seatGraphVoList;

    @Schema(description = "自习室门店名称")
    private String storeName;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区县名称")
    private String districtName;

}
