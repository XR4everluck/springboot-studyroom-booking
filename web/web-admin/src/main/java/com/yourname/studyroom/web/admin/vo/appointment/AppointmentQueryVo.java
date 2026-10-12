package com.yourname.studyroom.web.admin.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预约看房查询实体")
public class AppointmentQueryVo {


    @Schema(description="预约自习室门店所在省份")
    private Long provinceId;

    @Schema(description="预约自习室门店所在城市")
    private Long cityId;

    @Schema(description="预约自习室门店所在区")
    private Long districtId;

    @Schema(description="预约自习室门店所在自习室门店")
    private Long apartmentId;

    @Schema(description="预约用户姓名")
    private String name;

    @Schema(description="预约用户手机号码")
    private String phone;

}
