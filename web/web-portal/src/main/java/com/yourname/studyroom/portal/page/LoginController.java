package com.yourname.studyroom.portal.page;

import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.portal.config.SessionUser;
import com.yourname.studyroom.web.app.service.LoginService;
import com.yourname.studyroom.web.app.vo.user.RegisterVo;
import com.yourname.studyroom.web.admin.mapper.SystemUserMapper;
import com.yourname.studyroom.web.app.mapper.AppUserInfoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 统一登录/注册/登出：
 * 同一个登录界面按所选角色分别校验后台管理账号（system_user）与用户端账号（user_info），
 * 登录成功后写入 Session 并按角色跳转对应界面；未登录访问任何业务页面都会被拦截回登录页。
 */
@Controller
public class LoginController {

    private final SystemUserMapper systemUserMapper;
    private final AppUserInfoMapper userInfoMapper;
    private final LoginService appLoginService;
    private final StringRedisTemplate redisTemplate;

    /** 与用户端登录一致的时间抹平哈希：账号不存在时也执行一次同代价校验 */
    private static final String DUMMY_HASH = PasswordUtil.encode("dummy-password-for-timing");

    public LoginController(SystemUserMapper systemUserMapper,
                           AppUserInfoMapper userInfoMapper,
                           LoginService appLoginService,
                           StringRedisTemplate redisTemplate) {
        this.systemUserMapper = systemUserMapper;
        this.userInfoMapper = userInfoMapper;
        this.appLoginService = appLoginService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(defaultValue = "user") String role,
                            @RequestParam(required = false) String error,
                            @RequestParam(required = false) String registered,
                            @RequestParam(required = false) String username,
                            Model model) {
        model.addAttribute("role", "admin".equals(role) ? "admin" : "user");
        model.addAttribute("username", username == null ? "" : username);
        if ("auth".equals(error)) {
            model.addAttribute("error", "请先登录后再访问");
        } else if ("role".equals(error)) {
            model.addAttribute("error", "该账号无权访问此界面");
        }
        model.addAttribute("registered", registered != null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(defaultValue = "user") String role,
                        HttpSession session,
                        HttpServletRequest request,
                        RedirectAttributes ra) {
        try {
            if ("admin".equals(role)) {
                SystemUser systemUser = loginAsAdmin(username, password, request);
                session.setAttribute(SessionUser.SESSION_KEY,
                        new SessionUser(systemUser.getId(), systemUser.getUsername(), LoginUserType.ADMIN));
                recordAdminLogin(username, "成功", request);
                return "redirect:/admin/index";
            }
            UserInfo userInfo = loginAsAppUser(username, password);
            session.setAttribute(SessionUser.SESSION_KEY,
                    new SessionUser(userInfo.getId(), userInfo.getUsername(), LoginUserType.APP));
            return "redirect:/app/home";
        } catch (BookingException e) {
            // 错误提示走 flash；所选角色随 URL 回传，保证失败后页签停留在用户所选角色上
            if ("admin".equals(role)) {
                recordAdminLogin(username, "失败", request);
            }
            ra.addFlashAttribute("error", e.getMessage());
            ra.addAttribute("role", role);
            ra.addAttribute("username", username == null ? "" : username);
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterVo registerVo, RedirectAttributes ra) {
        try {
            appLoginService.register(registerVo);
        } catch (BookingException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("form", registerVo);
            return "redirect:/register";
        }
        ra.addFlashAttribute("registered", true);
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * 后台管理账号校验：失败计数锁定（与用户端同机制）→ PBKDF2 优先，存量 MD5 哈希校验通过后自动升级
     */
    private SystemUser loginAsAdmin(String username, String password, HttpServletRequest request) {
        String failKey = RedisConstant.ADMIN_LOGIN_FAIL_PREFIX + username;
        String count = redisTemplate.opsForValue().get(failKey);
        if (count != null && Long.parseLong(count) >= RedisConstant.ADMIN_LOGIN_FAIL_MAX) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
        }
        SystemUser systemUser = systemUserMapper.selectOneByUsername(username);
        if (systemUser == null) {
            recordAdminFailure(failKey, username);
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        if (systemUser.getStatus() == BaseStatus.DISABLE) {
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }
        String stored = systemUser.getPassword();
        boolean matched;
        if (stored != null && stored.startsWith("pbkdf2$")) {
            matched = PasswordUtil.matches(password, stored);
        } else {
            matched = stored != null && stored.equals(DigestUtils.md5Hex(password));
            if (matched) {
                SystemUser upgrade = new SystemUser();
                upgrade.setId(systemUser.getId());
                upgrade.setPassword(PasswordUtil.encode(password));
                systemUserMapper.updateById(upgrade);
            }
        }
        if (!matched) {
            long remain = recordAdminFailure(failKey, username);
            if (remain <= 0) {
                throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
            }
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        redisTemplate.delete(failKey);
        return systemUser;
    }

    /**
     * 记录一次管理端登录失败，返回剩余可尝试次数
     */
    private long recordAdminFailure(String failKey, String username) {
        Long current = redisTemplate.opsForValue().increment(failKey);
        redisTemplate.expire(failKey, RedisConstant.ADMIN_LOGIN_FAIL_TTL_SEC, TimeUnit.SECONDS);
        return current == null
                ? RedisConstant.ADMIN_LOGIN_FAIL_MAX - 1
                : RedisConstant.ADMIN_LOGIN_FAIL_MAX - current;
    }

    /**
     * 记录管理端登录流水（成功/失败），仅保留最近 N 条，用于看板"最近登录"展示
     */
    private void recordAdminLogin(String username, String result, HttpServletRequest request) {
        String time = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String entry = time + "|" + (username == null || username.isBlank() ? "-" : username)
                + "|" + result + "|" + request.getRemoteAddr();
        String key = RedisConstant.ADMIN_LOGIN_LOG_PREFIX + "recent";
        redisTemplate.opsForList().leftPush(key, entry);
        redisTemplate.opsForList().trim(key, 0, RedisConstant.ADMIN_LOGIN_LOG_KEEP - 1);
    }

    /**
     * 用户端账号校验：连续失败锁定（Redis 计数）→ 防枚举校验 → 状态检查
     */
    private UserInfo loginAsAppUser(String username, String password) {
        String failKey = RedisConstant.APP_LOGIN_FAIL_PREFIX + username;
        String count = redisTemplate.opsForValue().get(failKey);
        if (count != null && Long.parseLong(count) >= RedisConstant.APP_LOGIN_FAIL_MAX) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
        }
        UserInfo userInfo = userInfoMapper.selectByUsername(username);
        if (!PasswordUtil.matches(password, userInfo == null ? DUMMY_HASH : userInfo.getPassword())) {
            Long current = redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, RedisConstant.APP_LOGIN_FAIL_TTL_SEC, TimeUnit.SECONDS);
            if (current != null && current >= RedisConstant.APP_LOGIN_FAIL_MAX) {
                throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
            }
            throw new BookingException(ResultCodeEnum.APP_LOGIN_ERROR);
        }
        if (userInfo.getStatus() == BaseStatus.DISABLE) {
            throw new BookingException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }
        redisTemplate.delete(failKey);
        return userInfo;
    }
}
