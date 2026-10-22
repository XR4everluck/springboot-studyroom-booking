package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserItemVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author liubo
* @description 针对表【system_user(员工信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.SystemUser
*/
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    IPage<SystemUserItemVo> pageSystemUser(IPage<SystemUserItemVo> page, SystemUserQueryVo queryVo);

    SystemUser selectOneByUsername(String username);
}




