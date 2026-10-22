package com.yourname.studyroom.web.app.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "APP端修改密码实体")
public class ChangePasswordVo {

    @Schema(description = "原密码")
    private String oldPassword;

    @Schema(description = "新密码，8-20位且包含字母和数字")
    private String newPassword;

    @Schema(description = "确认新密码（服务端二次校验，防止前端被绕过）")
    private String confirmPassword;
}
