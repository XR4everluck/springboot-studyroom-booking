package com.yourname.studyroom.web.app.vo.appointment;

import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "APP端预约看房详情")
public class AppointmentDetailVo extends VisitAppointment {

    @Schema(description = "自习室门店基本信息")
    private ApartmentItemVo apartmentItemVo;
}
