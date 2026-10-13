package com.yourname.studyroom.web.admin.vo.agreement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预订订单查询实体")
public class AgreementQueryVo {

    @Schema(description = "自习室门店所处省份id")
    private Long provinceId;

    @Schema(description = "自习室门店所处城市id")
    private Long cityId;

    @Schema(description = "自习室门店所处区域id")
    private Long districtId;

    @Schema(description = "自习室门店id")
    private Long storeId;

    @Schema(description = "座位号")
    private String seatCode;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户手机号码")
    private String phone;

}
