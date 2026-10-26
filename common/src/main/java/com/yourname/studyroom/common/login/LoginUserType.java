package com.yourname.studyroom.common.login;

/**
 * 登录用户类型：写入 JWT 的 userType claim，
 * 用于拦截器区分管理端与用户端令牌，防止两类令牌互相越端调用
 */
public enum LoginUserType {

    ADMIN("admin"),

    APP("app");

    private final String code;

    LoginUserType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 根据 claim 值解析用户类型，无法识别时返回 null
     */
    public static LoginUserType fromCode(String code) {
        for (LoginUserType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
