package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserItemVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author liubo
* @description 针对表【system_user(员工信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.SystemUser
*/
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    IPage<SystemUserItemVo> pageSystemUser(IPage<SystemUserItemVo> page, SystemUserQueryVo queryVo);

    SystemUser selectOneByUsername(String username);

    /**
     * 查询用户端账号（user_info，与 system_user 同库）的密码哈希，
     * 供管理端登录失败时识别账号归属端并给出引导提示
     */
    @Select("SELECT password FROM user_info WHERE username = #{username} AND is_deleted = 0")
    String selectAppUserPasswordByUsername(@Param("username") String username);
}




