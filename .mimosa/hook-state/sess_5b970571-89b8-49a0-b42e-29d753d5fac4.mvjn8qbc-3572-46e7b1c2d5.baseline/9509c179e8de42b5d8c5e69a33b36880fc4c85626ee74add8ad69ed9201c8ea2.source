package com.yourname.studyroom.web.admin.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentQueryVo;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author liubo
* @description 针对表【visit_appointment(到店体验预约信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.VisitAppointment
*/
public interface VisitAppointmentMapper extends BaseMapper<VisitAppointment> {

    IPage<AppointmentVo> pageAppointment(Page<AppointmentVo> page, AppointmentQueryVo queryVo);
}




