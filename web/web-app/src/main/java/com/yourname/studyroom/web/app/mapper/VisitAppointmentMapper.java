package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【visit_appointment(到店体验预约信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.VisitAppointment
*/
public interface VisitAppointmentMapper extends BaseMapper<VisitAppointment> {

    List<AppointmentItemVo> listItemByUserId(Long userId);
}




