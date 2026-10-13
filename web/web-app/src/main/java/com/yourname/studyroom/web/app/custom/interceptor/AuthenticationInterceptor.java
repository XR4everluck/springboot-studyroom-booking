package com.yourname.studyroom.web.app.custom.interceptor;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("access-token");

        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (BookingException e) {
            ResponseUtil.writeFail(response, e);
            return false;
        }
        Long userId = claims.get("userId", Long.class);
        // 令牌中的键与 JwtUtil.createToken 保持一致，为 userName
        String username = claims.get("userName", String.class);
        LoginUserHolder.setLoginUser(new LoginUser(userId, username));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}