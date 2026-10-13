package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.app.mapper.VisitAppointmentMapper;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.VisitAppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentDetailVo;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【visit_appointment(到店体验预约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class VisitAppointmentServiceImpl extends ServiceImpl<VisitAppointmentMapper, VisitAppointment>
        implements VisitAppointmentService {

    @Autowired
    private VisitAppointmentMapper visitAppointmentMapper;

    @Autowired
    private StudyStoreService studyStoreService;

    /**
     * 根据用户ID查询个人到店体验预约列表
     *
     * @param userId 用户ID
     * @return 个人到店体验预约列表
     */
    @Override
    public List<AppointmentItemVo> listItemByUserId(Long userId) {
        return visitAppointmentMapper.listItemByUserId(userId);
    }

    /**
     * 根据ID获取到店体验预约详情
     *
     * @param id 到店体验预约ID
     * @return 到店体验预约详情对象
     */
    @Override
    public AppointmentDetailVo getDetailById(Long id) {
        VisitAppointment visitAppointment = visitAppointmentMapper.selectById(id);

        ApartmentItemVo apartmentItemVo = studyStoreService.selectApartmentItemVoById(visitAppointment.getStoreId());

        AppointmentDetailVo agreementDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(visitAppointment, agreementDetailVo);

        agreementDetailVo.setApartmentItemVo(apartmentItemVo);

        return agreementDetailVo;
    }
}




