package com.yourname.studyroom.web.app.controller.appointment;


import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
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
    @Operation(summary = "保存或更新到店体验预约")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody VisitAppointment visitAppointment) {
        visitAppointment.setUserId(LoginUserHolder.getLoginUser().getUserId());
        visitAppointmentService.saveOrUpdate(visitAppointment);
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
        AppointmentDetailVo result = visitAppointmentService.getDetailById(id);
        return Result.ok(result);
    }

}

