package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.model.enums.SystemUserType;
import com.yourname.studyroom.web.admin.service.SystemPostService;
import com.yourname.studyroom.web.admin.service.SystemUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理端系统账号（后台管理员）管理：分页、新增/编辑（密码 PBKDF2 存储）、删除
 */
@Controller
@RequestMapping("/admin/sysuser")
public class AdminSystemUserController {

    private final SystemUserService systemUserService;
    private final SystemPostService systemPostService;

    public AdminSystemUserController(SystemUserService systemUserService,
                                     SystemPostService systemPostService) {
        this.systemUserService = systemUserService;
        this.systemPostService = systemPostService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       @RequestParam(required = false) String username,
                       Model model) {
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isBlank()) {
            wrapper.like(SystemUser::getUsername, username.trim());
        }
        wrapper.orderByAsc(SystemUser::getId);
        model.addAttribute("page", systemUserService.page(new Page<>(current, size), wrapper));
        model.addAttribute("username", username);
        model.addAttribute("posts", systemPostService.list());
        return "admin/sysuser-list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        SystemUser systemUser = id == null ? new SystemUser() : systemUserService.getById(id);
        if (systemUser == null) {
            systemUser = new SystemUser();
        }
        model.addAttribute("systemUser", systemUser);
        model.addAttribute("posts", systemPostService.list());
        return "admin/sysuser-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String username,
                       @RequestParam String name,
                       @RequestParam SystemUserType type,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Long postId,
                       @RequestParam(defaultValue = "true") boolean enable,
                       @RequestParam(required = false) String password,
                       RedirectAttributes ra) {
        SystemUser systemUser = id == null ? new SystemUser() : systemUserService.getById(id);
        if (systemUser == null) {
            ra.addFlashAttribute("error", "账号不存在");
            return "redirect:/admin/sysuser";
        }
        // 新增时必须设置初始密码；编辑时留空表示不修改密码
        if (id == null) {
            if (password == null || password.isBlank()) {
                ra.addFlashAttribute("error", "新增账号必须设置初始密码");
                return "redirect:/admin/sysuser/form";
            }
            systemUser.setPassword(PasswordUtil.encode(password));
        } else if (password != null && !password.isBlank()) {
            systemUser.setPassword(PasswordUtil.encode(password));
        }
        systemUser.setUsername(username);
        systemUser.setName(name);
        systemUser.setType(type);
        systemUser.setPhone(phone);
        systemUser.setPostId(postId);
        systemUser.setStatus(enable ? BaseStatus.ENABLE : BaseStatus.DISABLE);
        systemUserService.saveOrUpdate(systemUser);
        ra.addFlashAttribute("msg", "账号已保存");
        return "redirect:/admin/sysuser";
    }

    /**
     * 删除确认页（无 JS 架构下的防误删中转）
     */
    @GetMapping("/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        SystemUser systemUser = systemUserService.getById(id);
        model.addAttribute("confirmTitle", "删除系统账号");
        model.addAttribute("confirmMessage", systemUser == null
                ? "该账号不存在，可能已被删除。"
                : "确认删除后台账号「" + systemUser.getUsername() + "（" + systemUser.getName() + "）」？删除后该账号将无法登录管理端。");
        model.addAttribute("confirmAction", "/admin/sysuser/delete/" + id);
        model.addAttribute("confirmButtonText", "确认删除账号");
        model.addAttribute("cancelUrl", "/admin/sysuser");
        return "confirm";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        systemUserService.removeById(id);
        ra.addFlashAttribute("msg", "账号已删除");
        return "redirect:/admin/sysuser";
    }
}
