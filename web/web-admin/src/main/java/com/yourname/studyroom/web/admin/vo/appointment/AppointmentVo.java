package com.yourname.studyroom.web.admin.vo.appointment;

import com.yourname.studyroom.model.entity.ApartmentInfo;
import com.yourname.studyroom.model.entity.ViewAppointment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预约看房信息")
public class AppointmentVo extends ViewAppointment {

    @Schema(description = "预约自习室门店信息")
    private ApartmentInfo apartmentInfo;

}
