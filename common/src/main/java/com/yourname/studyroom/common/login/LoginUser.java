package com.yourname.studyroom.common.login;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ThreadLocal的数据类
 */
@Data
@AllArgsConstructor
public class LoginUser {
    private Long userId;
    private String username;
}