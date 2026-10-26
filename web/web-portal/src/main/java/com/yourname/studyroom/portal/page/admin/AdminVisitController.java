package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.AppointmentStatus;
import com.yourname.studyroom.web.admin.service.VisitAppointmentService;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentQueryVo;
import com.yourname.studyroom.web.admin.vo.appointment.AppointmentVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理端到店体验预约管理：分页列表与状态处理（确认已体验/取消）
 */
@Controller
@RequestMapping("/admin/visit")
public class AdminVisitController {

    private final VisitAppointmentService visitAppointmentService;

    public AdminVisitController(VisitAppointmentService visitAppointmentService) {
        this.visitAppointmentService = visitAppointmentService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Long storeId,
                       @RequestParam(required = false) AppointmentStatus status,
                       Model model) {
        AppointmentQueryVo queryVo = new AppointmentQueryVo();
        queryVo.setName(name);
        queryVo.setPhone(phone);
        queryVo.setStoreId(storeId);
        queryVo.setAppointmentStatus(status);
        Page<AppointmentVo> page = new Page<>(current, size);
        model.addAttribute("page", visitAppointmentService.pageAppointment(page, queryVo));
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("storeId", storeId);
        model.addAttribute("status", status);
        return "admin/visit-list";
    }

    @PostMapping("/status/{id}")
    public String status(@PathVariable Long id,
                         @RequestParam AppointmentStatus status,
                         RedirectAttributes ra) {
        LambdaUpdateWrapper<VisitAppointment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(VisitAppointment::getId, id);
        wrapper.set(VisitAppointment::getAppointmentStatus, status);
        visitAppointmentService.update(wrapper);
        ra.addFlashAttribute("msg", "预约状态已更新为「" + status.getName() + "」");
        return "redirect:/admin/visit";
    }
}
