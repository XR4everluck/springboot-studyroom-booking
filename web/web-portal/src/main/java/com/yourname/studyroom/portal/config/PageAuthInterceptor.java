package com.yourname.studyroom.portal.config;

import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 页面访问认证拦截器：
 * 未登录访问任何页面一律重定向到统一登录页；
 * 已登录但角色不符（如用户账号访问 /admin/**）也退回登录页。
 * 通过校验后写入 LoginUserHolder，供页面控制器获取当前登录用户。
 */
public class PageAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        SessionUser user = session == null ? null : (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
        String uri = request.getRequestURI();

        if (user == null) {
            response.sendRedirect("/login?error=auth");
            return false;
        }
        if (uri.startsWith("/admin/") && !user.isAdmin()) {
            response.sendRedirect("/login?error=role");
            return false;
        }
        if (uri.startsWith("/app/") && user.isAdmin()) {
            // 管理员账号不进入用户端界面
            response.sendRedirect("/admin/index");
            return false;
        }
        LoginUserHolder.setLoginUser(new LoginUser(user.getUserId(), user.getUsername()));
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        LoginUserHolder.clear();
    }
}
