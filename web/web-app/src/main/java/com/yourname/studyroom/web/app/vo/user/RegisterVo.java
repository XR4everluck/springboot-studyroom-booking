package com.yourname.studyroom.web.app.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "APP端注册实体")
public class RegisterVo {

    @Schema(description = "登录账号，4-20位字母、数字或下划线")
    private String username;

    @Schema(description = "登录密码，8-20位且包含字母和数字")
    private String password;

    @Schema(description = "昵称，选填，1-12个字符，不传时默认使用登录账号")
    private String nickname;
}
