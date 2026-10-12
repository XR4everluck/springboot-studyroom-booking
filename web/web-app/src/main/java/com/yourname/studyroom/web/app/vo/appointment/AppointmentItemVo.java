package com.yourname.studyroom.web.app.vo.appointment;

import com.yourname.studyroom.model.enums.AppointmentStatus;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "APP端预约看房基本信息")
public class AppointmentItemVo {

    @Schema(description = "预约Id")
    private Long id;

    @Schema(description = "预约自习室门店名称")
    private String apartmentName;

    @Schema(description = "自习室门店图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appointmentTime;

    @Schema(description = "当前预约状态")
    private AppointmentStatus appointmentStatus;
}
