package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.web.admin.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理端用户管理：分页列表与启用/禁用
 */
@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private final UserInfoService userInfoService;

    public AdminUserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) BaseStatus status,
                       Model model) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (phone != null && !phone.isBlank()) {
            wrapper.like(UserInfo::getPhone, phone.trim());
        }
        if (status != null) {
            wrapper.eq(UserInfo::getStatus, status);
        }
        wrapper.orderByDesc(UserInfo::getId);
        model.addAttribute("page", userInfoService.page(new Page<>(current, size), wrapper));
        model.addAttribute("phone", phone);
        model.addAttribute("status", status);
        return "admin/user-list";
    }

    @PostMapping("/status/{id}")
    public String status(@PathVariable Long id,
                         @RequestParam boolean enable,
                         RedirectAttributes ra) {
        UserInfo patch = new UserInfo();
        patch.setId(id);
        patch.setStatus(enable ? BaseStatus.ENABLE : BaseStatus.DISABLE);
        userInfoService.updateById(patch);
        ra.addFlashAttribute("msg", enable ? "用户已启用" : "用户已禁用");
        return "redirect:/admin/user";
    }
}
