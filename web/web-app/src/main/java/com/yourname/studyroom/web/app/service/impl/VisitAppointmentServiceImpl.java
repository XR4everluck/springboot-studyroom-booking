package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.BusinessHoursUtil;
import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.app.mapper.AppVisitAppointmentMapper;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.VisitAppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.store.StoreItemVo;
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
public class VisitAppointmentServiceImpl extends ServiceImpl<AppVisitAppointmentMapper, VisitAppointment>
        implements VisitAppointmentService {

    @Autowired
    private AppVisitAppointmentMapper visitAppointmentMapper;

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

        StoreItemVo storeItemVo = studyStoreService.selectStoreItemVoById(visitAppointment.getStoreId());

        AppointmentDetailVo orderDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(visitAppointment, orderDetailVo);

        orderDetailVo.setStoreItemVo(storeItemVo);

        return orderDetailVo;
    }

    /**
     * 创建或更新到店体验预约：预约时间需落在门店营业时间内
     */
    @Override
    public void createOrUpdateAppointment(VisitAppointment visitAppointment) {
        StudyStore studyStore = studyStoreService.getById(visitAppointment.getStoreId());
        if (studyStore == null) {
            throw new BookingException(ResultCodeEnum.DATA_ERROR);
        }
        boolean withinBusinessHours = BusinessHoursUtil.isWithin(
                studyStore.getBusinessHours(),
                visitAppointment.getAppointmentTime());
        if (!withinBusinessHours) {
            throw new BookingException(ResultCodeEnum.APPOINTMENT_NOT_IN_BUSINESS_HOURS);
        }
        this.saveOrUpdate(visitAppointment);
    }
}




