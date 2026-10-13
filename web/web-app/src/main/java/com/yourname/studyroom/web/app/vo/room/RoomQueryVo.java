package com.yourname.studyroom.web.app.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "座位查询实体")
public class RoomQueryVo {


    @Schema(description = "省份Id")
    private Long provinceId;

    @Schema(description = "城市Id")
    private Long cityId;

    @Schema(description = "区域Id")
    private Long districtId;

    @Schema(description = "最低座位单价")
    private BigDecimal minPrice;

    @Schema(description = "最高座位单价")
    private BigDecimal maxPrice;

    @Schema(description = "支付方式")
    private Long paymentTypeId;

    @Schema(description = "价格排序方式", allowableValues = {"desc", "asc"})
    private String orderType;

}
