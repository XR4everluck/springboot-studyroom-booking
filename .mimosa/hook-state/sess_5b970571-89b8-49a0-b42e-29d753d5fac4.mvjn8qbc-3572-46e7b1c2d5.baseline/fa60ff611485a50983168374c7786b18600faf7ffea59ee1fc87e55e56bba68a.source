package com.yourname.studyroom.model.entity;

import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "自习室门店信息表")
@TableName(value = "study_store")
@Data
public class StudyStore extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自习室门店名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "自习室门店介绍")
    @TableField(value = "introduction")
    private String introduction;

    @Schema(description = "所处区域id")
    @TableField(value = "district_id")
    private Long districtId;

    @Schema(description = "所处区域名称")
    @TableField(value = "district_name")
    private String districtName;

    @Schema(description = "所处城市id")
    @TableField(value = "city_id")
    private Long cityId;

    @Schema(description = "所处城市名称")
    @TableField(value = "city_name")
    private String cityName;

    @Schema(description = "所处省份id")
    @TableField(value = "province_id")
    private Long provinceId;

    @Schema(description = "所处省份名称")
    @TableField(value = "province_name")
    private String provinceName;

    @Schema(description = "详细地址")
    @TableField(value = "address_detail")
    private String addressDetail;

    @Schema(description = "经度")
    @TableField(value = "latitude")
    private String latitude;

    @Schema(description = "纬度")
    @TableField(value = "longitude")
    private String longitude;

    @Schema(description = "自习室门店前台电话")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "营业时间，如 08:00-22:00")
    @TableField(value = "business_hours")
    private String businessHours;

    @Schema(description = "座位总数")
    @TableField(value = "seat_count")
    private Integer seatCount;

    @Schema(description = "是否发布")
    @TableField(value = "is_release")
    private ReleaseStatus isRelease;

}
