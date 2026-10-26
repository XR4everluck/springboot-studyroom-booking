package com.yourname.studyroom.portal.page.app;

import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.web.app.service.LoginService;
import com.yourname.studyroom.web.app.vo.user.ChangePasswordVo;
import com.yourname.studyroom.web.app.vo.user.UserInfoVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 用户端个人中心：资料展示、修改密码（改密后强制重新登录）
 */
@Controller
public class AppProfileController {

    private final LoginService appLoginService;

    public AppProfileController(LoginService appLoginService) {
        this.appLoginService = appLoginService;
    }

    @GetMapping("/app/profile")
    public String profile(Model model) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        UserInfoVo userInfo = appLoginService.getLogInUserById(userId);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("username", LoginUserHolder.getLoginUser().getUsername());
        return "app/profile";
    }

    @PostMapping("/app/profile/password")
    public String changePassword(ChangePasswordVo changePasswordVo,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        try {
            appLoginService.changePassword(userId, changePasswordVo);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/profile";
        }
        // 密码修改成功后强制重新登录
        session.invalidate();
        ra.addFlashAttribute("msg", "密码修改成功，请使用新密码重新登录");
        return "redirect:/login";
    }
}
