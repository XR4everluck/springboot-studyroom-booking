package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.model.entity.VisitAppointment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentDetailVo;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【visit_appointment(到店体验预约信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface VisitAppointmentService extends IService<VisitAppointment> {
    List<AppointmentItemVo> listItemByUserId(Long userId);

    AppointmentDetailVo getDetailById(Long id);

    /**
     * 创建或更新到店体验预约，校验预约时间是否落在门店营业时间内
     *
     * @param visitAppointment 到店体验预约
     */
    void createOrUpdateAppointment(VisitAppointment visitAppointment);
}
