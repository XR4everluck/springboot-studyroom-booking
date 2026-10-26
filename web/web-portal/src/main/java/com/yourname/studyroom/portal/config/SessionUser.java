package com.yourname.studyroom.portal.config;

import com.yourname.studyroom.common.login.LoginUserType;

import java.io.Serializable;

/**
 * 会话中的登录用户（Thymeleaf 页面体系使用 HttpSession 认证，替代原 JWT 方案）
 */
public class SessionUser implements Serializable {

    public static final String SESSION_KEY = "LOGIN_USER";

    private final Long userId;
    private final String username;
    private final LoginUserType userType;

    public SessionUser(Long userId, String username, LoginUserType userType) {
        this.userId = userId;
        this.username = username;
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LoginUserType getUserType() {
        return userType;
    }

    public boolean isAdmin() {
        return userType == LoginUserType.ADMIN;
    }
}
