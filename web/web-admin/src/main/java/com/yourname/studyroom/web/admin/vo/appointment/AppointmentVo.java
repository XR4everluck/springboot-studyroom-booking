package com.yourname.studyroom.web.admin.vo.appointment;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.VisitAppointment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预约看房信息")
public class AppointmentVo extends VisitAppointment {

    @Schema(description = "预约自习室门店信息")
    private StudyStore apartmentInfo;

}
