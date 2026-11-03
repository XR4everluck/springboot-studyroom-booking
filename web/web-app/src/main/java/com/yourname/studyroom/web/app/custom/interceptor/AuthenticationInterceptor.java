package com.yourname.studyroom.web.app.custom.interceptor;

import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

/**
 * 登录状态校验拦截器
 * <p>
 * 除验签外还校验 userType 必须为 app，
 * 防止管理端令牌跨端调用用户端接口；
 * 并比对令牌签发时间与最近一次改密时间，使修改密码前的旧令牌立即失效
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("access-token");

        Long userId;
        String username;
        try {
            Claims claims = JwtUtil.parseToken(token);
            JwtUtil.checkUserType(claims, LoginUserType.APP);
            // 令牌中的键与 JwtUtil.createToken 保持一致
            userId = claims.get("userId", Long.class);
            username = claims.get("userName", String.class);
            if (isRevokedByPasswordChange(userId, claims)) {
                throw new BookingException(ResultCodeEnum.APP_PASSWORD_NOT_LOGIN);
            }
        } catch (BookingException e) {
            ResponseUtil.writeFail(response, e);
            return false;
        }
        LoginUserHolder.setLoginUser(new LoginUser(userId, username));

        return true;
    }

    /**
     * 判断令牌是否因密码变更而失效：签发时间早于最近一次改密时间即视为失效
     * <p>
     * Redis 不可用时放行，避免缓存故障导致用户端整体不可用；
     * 未携带 iat 的历史令牌不做拦截，仍按原有效期自然过期
     */
    private boolean isRevokedByPasswordChange(Long userId, Claims claims) {
        if (userId == null) {
            return false;
        }
        try {
            String changedAt = redisTemplate.opsForValue()
                    .get(RedisConstant.APP_PASSWORD_CHANGED_PREFIX + userId);
            if (changedAt == null) {
                return false;
            }
            Date issuedAt = claims.getIssuedAt();
            if (issuedAt == null) {
                return false;
            }
            return issuedAt.getTime() / 1000 < Long.parseLong(changedAt);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
