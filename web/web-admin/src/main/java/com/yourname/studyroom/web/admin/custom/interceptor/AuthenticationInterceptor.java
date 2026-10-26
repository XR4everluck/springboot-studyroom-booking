package com.yourname.studyroom.web.admin.custom.interceptor;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录状态校验拦截器
 * <p>
 * 除验签外还校验 userType 必须为 admin，
 * 防止用户端令牌跨端调用管理端接口
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");
        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
            JwtUtil.checkUserType(claims, LoginUserType.ADMIN);
        } catch (BookingException e) {
            ResponseUtil.writeFail(response, e);
            return false;
        }
        Long userId = claims.get("userId", Long.class);
        String userName = claims.get("userName", String.class);
        LoginUserHolder.setLoginUser(new LoginUser(userId,userName));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
