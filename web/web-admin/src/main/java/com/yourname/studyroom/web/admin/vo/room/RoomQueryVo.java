package com.yourname.studyroom.web.admin.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "座位查询实体")
@Data
public class RoomQueryVo {

    @Schema(description = "省份Id")
    private Long provinceId;

    @Schema(description = "城市Id")
    private Long cityId;

    @Schema(description = "区域Id")
    private Long districtId;

    @Schema(description = "自习室门店Id")
    private Long storeId;
}
