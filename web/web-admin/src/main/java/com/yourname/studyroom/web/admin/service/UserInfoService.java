package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 管理员核验用户身份后重置密码：生成临时密码、置强制改密标记并使旧令牌立即失效
     *
     * @param id 用户ID
     * @return 临时明文密码（仅本次响应返回，由管理员当场告知用户）
     */
    String resetPasswordById(Long id);
}
