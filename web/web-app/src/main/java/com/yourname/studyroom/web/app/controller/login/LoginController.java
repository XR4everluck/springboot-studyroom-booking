package com.yourname.studyroom.web.app.controller.login;


import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.web.app.service.LoginService;
import com.yourname.studyroom.web.app.vo.user.LoginVo;
import com.yourname.studyroom.web.app.vo.user.RegisterVo;
import com.yourname.studyroom.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理")
@RestController
@RequestMapping("/app/")
public class LoginController {

    @Autowired
    LoginService loginService;

    /**
     * 登录接口
     *
     * @param loginVo 登录信息对象，包含账号与密码
     * @return 登录成功后返回的包含JWT令牌的Result对象
     */
    @PostMapping("login")
    @Operation(summary = "登录", description = "账号密码登录，连续失败5次锁定10分钟")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token = loginService.login(loginVo);
        return Result.ok(token);
    }

    /**
     * 注册接口
     *
     * @param registerVo 注册信息对象，包含账号、密码、昵称与选填手机号
     * @return 操作结果
     */
    @PostMapping("register")
    @Operation(summary = "注册", description = "账号密码注册，成功后可直接登录")
    public Result<Void> register(@RequestBody RegisterVo registerVo) {
        loginService.register(registerVo);
        return Result.ok();
    }

    /**
     * 获取登录用户信息
     *
     * @return 包含用户信息的Result对象
     */
    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    public Result<UserInfoVo> info() {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        UserInfoVo result = loginService.getLogInUserById(userId);
        return Result.ok(result);
    }
}
