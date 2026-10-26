package com.yourname.studyroom.portal.page.app;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.model.entity.VisitAppointment;
import com.yourname.studyroom.model.enums.AppointmentStatus;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.VisitAppointmentService;
import com.yourname.studyroom.web.app.vo.appointment.AppointmentItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户端到店体验预约：新建预约、我的预约列表、取消预约
 */
@Controller
public class AppVisitController {

    private final VisitAppointmentService appVisitAppointmentService;
    private final StudyStoreService adminStudyStoreService;

    public AppVisitController(VisitAppointmentService appVisitAppointmentService,
                              StudyStoreService adminStudyStoreService) {
        this.appVisitAppointmentService = appVisitAppointmentService;
        this.adminStudyStoreService = adminStudyStoreService;
    }

    @GetMapping("/app/visit")
    public String page(Model model) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        List<AppointmentItemVo> list = appVisitAppointmentService.listItemByUserId(userId);
        model.addAttribute("appointments", list);
        model.addAttribute("stores", adminStudyStoreService.list());
        return "app/visit";
    }

    @PostMapping("/app/visit/save")
    public String save(@RequestParam Long storeId,
                       @RequestParam String appointmentTime,
                       @RequestParam(required = false) String additionalInfo,
                       RedirectAttributes ra) {
        Date time;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(appointmentTime);
        } catch (ParseException e) {
            ra.addFlashAttribute("error", "时间格式不正确");
            return "redirect:/app/visit";
        }
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        VisitAppointment appointment = new VisitAppointment();
        appointment.setUserId(userId);
        appointment.setStoreId(storeId);
        appointment.setAppointmentTime(time);
        appointment.setAdditionalInfo(additionalInfo);
        appointment.setAppointmentStatus(AppointmentStatus.WAITING);
        try {
            appVisitAppointmentService.createOrUpdateAppointment(appointment);
            ra.addFlashAttribute("msg", "预约成功，请按时到店体验");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/visit";
    }

    @PostMapping("/app/visit/cancel/{id}")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        VisitAppointment appointment = appVisitAppointmentService.getById(id);
        if (appointment == null || !userId.equals(appointment.getUserId())) {
            ra.addFlashAttribute("error", "预约不存在");
            return "redirect:/app/visit";
        }
        LambdaUpdateWrapper<VisitAppointment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(VisitAppointment::getId, id);
        wrapper.set(VisitAppointment::getAppointmentStatus, AppointmentStatus.CANCELED);
        appVisitAppointmentService.update(wrapper);
        ra.addFlashAttribute("msg", "预约已取消");
        return "redirect:/app/visit";
    }
}
