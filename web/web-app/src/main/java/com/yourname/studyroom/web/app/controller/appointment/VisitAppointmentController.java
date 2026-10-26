package com.yourname.studyroom.web.app.controller.appointment;


import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.web.app.service.VisitAppointmentService;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentDetailVo;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "到店体验预约信息")
@RestController
@RequestMapping("/app/visit")
public class VisitAppointmentController {
    @Autowired
    private VisitAppointmentService visitAppointmentService;

    /**
     * 保存或更新到店体验预约
     *
     * @param visitAppointment 到店体验预约对象
     * @return 操作结果
     */
    @Operation(summary = "保存或更新到店体验预约", description = "预约时间需在门店营业时间范围内")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody VisitAppointment visitAppointment) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        // 更新时校验预约归属，防止冒改他人预约；新预约 id 为空无需校验
        if (visitAppointment.getId() != null) {
            checkOwned(userId, visitAppointment.getId());
        }
        visitAppointment.setUserId(userId);
        visitAppointmentService.createOrUpdateAppointment(visitAppointment);
        return Result.ok();
    }

    /**
     * 查询个人到店体验预约列表
     *
     * @return 个人到店体验预约列表
     */
    @Operation(summary = "查询个人到店体验预约列表")
    @GetMapping("listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        List<AppointmentItemVo> result = visitAppointmentService.listItemByUserId(LoginUserHolder.getLoginUser().getUserId());
        return Result.ok(result);
    }

    /**
     * 根据ID查询预约详情信息
     *
     * @param id 预约详情ID
     * @return 预约详情信息
     */
    @GetMapping("getDetailById")
    @Operation(summary = "根据ID查询预约详情信息")
    public Result<AppointmentDetailVo> getDetailById(Long id) {
        // 先校验预约归属，防止查看他人预约
        checkOwned(LoginUserHolder.getLoginUser().getUserId(), id);
        AppointmentDetailVo result = visitAppointmentService.getDetailById(id);
        return Result.ok(result);
    }

    /**
     * 校验预约存在且属于当前用户，不满足时统一抛"预约不存在"，避免越权探测他人预约
     */
    private void checkOwned(Long userId, Long id) {
        VisitAppointment appointment = visitAppointmentService.getById(id);
        if (appointment == null || !userId.equals(appointment.getUserId())) {
            throw new BookingException(ResultCodeEnum.DATA_ERROR);
        }
    }

}

