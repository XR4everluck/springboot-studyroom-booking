package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.entity.SystemPost;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.web.admin.service.SystemPostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理端岗位管理：分页、新增/编辑、删除
 */
@Controller
@RequestMapping("/admin/post")
public class AdminSystemPostController {

    private final SystemPostService systemPostService;

    public AdminSystemPostController(SystemPostService systemPostService) {
        this.systemPostService = systemPostService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       Model model) {
        model.addAttribute("page", systemPostService.page(new Page<>(current, size)));
        return "admin/post-list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        SystemPost post = id == null ? new SystemPost() : systemPostService.getById(id);
        if (post == null) {
            post = new SystemPost();
        }
        model.addAttribute("post", post);
        return "admin/post-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String postCode,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam(defaultValue = "true") boolean enable,
                       RedirectAttributes ra) {
        SystemPost post = id == null ? new SystemPost() : systemPostService.getById(id);
        if (post == null) {
            post = new SystemPost();
        }
        post.setId(id);
        post.setPostCode(postCode);
        post.setName(name);
        post.setDescription(description);
        post.setStatus(enable ? BaseStatus.ENABLE : BaseStatus.DISABLE);
        systemPostService.saveOrUpdate(post);
        ra.addFlashAttribute("msg", "岗位已保存");
        return "redirect:/admin/post";
    }

    /**
     * 删除确认页（无 JS 架构下的防误删中转）
     */
    @GetMapping("/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        SystemPost post = systemPostService.getById(id);
        model.addAttribute("confirmTitle", "删除岗位");
        model.addAttribute("confirmMessage", post == null
                ? "该岗位不存在，可能已被删除。"
                : "确认删除岗位「" + post.getName() + "」？正在使用该岗位的账号将失去岗位归属。");
        model.addAttribute("confirmAction", "/admin/post/delete/" + id);
        model.addAttribute("confirmButtonText", "确认删除岗位");
        model.addAttribute("cancelUrl", "/admin/post");
        return "confirm";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        systemPostService.removeById(id);
        ra.addFlashAttribute("msg", "岗位已删除");
        return "redirect:/admin/post";
    }
}
