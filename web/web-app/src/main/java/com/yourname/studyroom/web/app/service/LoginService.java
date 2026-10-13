package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.web.app.vo.user.LoginVo;
import com.yourname.studyroom.web.app.vo.user.RegisterVo;
import com.yourname.studyroom.web.app.vo.user.UserInfoVo;

public interface LoginService {

    String login(LoginVo loginVo);

    void register(RegisterVo registerVo);

    UserInfoVo getLogInUserById(Long userId);
}
