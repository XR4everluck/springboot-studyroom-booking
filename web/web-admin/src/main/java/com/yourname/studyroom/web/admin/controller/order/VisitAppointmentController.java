package com.yourname.studyroom.web.admin.controller.booking;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.AppointmentStatus;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentQueryVo;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "到店体验预约管理")
@RequestMapping("/admin/visit")
@RestController
public class VisitAppointmentController {

    @Autowired
    VisitAppointmentService visitAppointmentService;

    /**
     * 分页查询预约信息
     *
     * @param current 当前页码
     * @param size    每页显示的记录数
     * @param queryVo 查询条件对象
     * @return 包含分页结果的Result对象
     */
    @Operation(summary = "分页查询预约信息")
    @GetMapping("page")
    public Result<IPage<AppointmentVo>> page(@RequestParam long current, @RequestParam long size, AppointmentQueryVo queryVo) {
        Page<AppointmentVo> page = new Page<>(current, size);
        IPage<AppointmentVo> result = visitAppointmentService.pageAppointment(page,queryVo);
        return Result.ok(result);
    }

    /**
     * 根据id更新预约状态
     *
     * @param id     预约的id
     * @param status 预约状态
     * @return Result对象，表示操作结果
     */
    @Operation(summary = "根据id更新预约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam AppointmentStatus status) {
        LambdaUpdateWrapper<VisitAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VisitAppointment::getId,id);
        updateWrapper.set(VisitAppointment::getAppointmentStatus,status);
        visitAppointmentService.update(updateWrapper);
        return Result.ok();
    }

}
