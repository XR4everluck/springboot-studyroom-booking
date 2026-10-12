package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.model.entity.VisitAppointment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentDetailVo;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【visit_appointment(预约看房信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface VisitAppointmentService extends IService<VisitAppointment> {
    List<AppointmentItemVo> listItemByUserId(Long userId);

    AppointmentDetailVo getDetailById(Long id);
}
