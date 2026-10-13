package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 按登录账号查询用户（显式带出 password 列，实体上该字段 select=false）
     */
    @Select("SELECT id, username, phone, password, avatar_url, nickname, status " +
            "FROM user_info WHERE username = #{username} AND is_deleted = 0")
    UserInfo selectByUsername(@Param("username") String username);
}
