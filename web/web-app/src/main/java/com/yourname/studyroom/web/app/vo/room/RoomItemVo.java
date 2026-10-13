package com.yourname.studyroom.web.app.vo.room;


import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "APP座位列表实体")
@Data
public class RoomItemVo {

    @Schema(description = "座位id")
    private Long id;

    @Schema(description = "座位号")
    private String seatCode;

    @Schema(description = "座位单价（元/月）")
    private BigDecimal rent;

    @Schema(description = "座位图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "座位标签列表")
    private List<StoreLabel> storeLabelList;

    @Schema(description = "座位所属自习室门店信息")
    private StudyStore studyStore;

    @Schema(description = "座位实时状态编码：0-空闲 1-已预订 2-使用中")
    private Integer realtimeStatus;

    @Schema(description = "座位实时状态名称")
    private String realtimeStatusName;

}
