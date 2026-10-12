package com.yourname.studyroom.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yourname.studyroom.model.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Schema(description = "到店体验预约信息表")
@TableName(value = "visit_appointment")
@Data
public class VisitAppointment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "用户姓名")
    @TableField(value = "name")
    private String name;

    @Schema(description = "用户手机号码")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "预约时间")
    @TableField(value = "appointment_time")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") //配置 JackSon序列化时间格式以及时区
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") //配置 JackSon序列化时间格式
    private Date appointmentTime;

    @Schema(description = "备注信息")
    @TableField(value = "additional_info")
    private String additionalInfo;

    @Schema(description = "预约状态")
    @TableField(value = "appointment_status")
    private AppointmentStatus appointmentStatus;
}
