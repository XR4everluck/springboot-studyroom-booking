package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.web.admin.vo.login.LoginVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    String login(LoginVo loginVo);

    SystemUserInfoVo getLoginUserInfo(Long userId);
}
