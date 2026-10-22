package com.yourname.studyroom.web.admin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自习室门店查询实体")
public class StoreQueryVo {

    @Schema(description = "省份id")
    private Long provinceId;

    @Schema(description = "城市id")
    private Long cityId;

    @Schema(description = "区域id")
    private Long districtId;
}
