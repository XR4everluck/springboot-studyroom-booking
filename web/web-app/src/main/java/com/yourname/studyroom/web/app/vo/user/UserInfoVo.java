package com.yourname.studyroom.web.app.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "用户基本信息")
@Data
@AllArgsConstructor
public class UserInfoVo {

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatarUrl;

    @Schema(description = "是否需强制修改密码 0否 1是（管理员重置密码后为1）")
    private Integer mustChangePwd;

}