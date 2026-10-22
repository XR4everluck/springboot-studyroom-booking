package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.admin.mapper.VisitAppointmentMapper;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentQueryVo;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【visit_appointment(到店体验预约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class VisitAppointmentServiceImpl extends ServiceImpl<VisitAppointmentMapper, VisitAppointment>
        implements VisitAppointmentService {

    @Autowired
    VisitAppointmentMapper visitAppointmentMapper;

    @Override
    public IPage<AppointmentVo> pageAppointment(Page<AppointmentVo> page, AppointmentQueryVo queryVo) {
        return visitAppointmentMapper.pageAppointment(page, queryVo);
    }
}




