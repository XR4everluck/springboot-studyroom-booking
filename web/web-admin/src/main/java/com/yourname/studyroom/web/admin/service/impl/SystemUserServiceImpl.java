package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.model.entity.SystemPost;
import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.web.admin.mapper.SystemPostMapper;
import com.yourname.studyroom.web.admin.mapper.SystemUserMapper;
import com.yourname.studyroom.web.admin.service.SystemUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserItemVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【system_user(员工信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>
        implements SystemUserService {

    @Autowired
    SystemUserMapper systemUserMapper;

    @Autowired
    SystemPostMapper systemPostMapper;

    @Override
    public IPage<SystemUserItemVo> pageSystemUser(IPage<SystemUserItemVo> page, SystemUserQueryVo queryVo) {
        return systemUserMapper.pageSystemUser(page,queryVo);
    }

    @Override
    public SystemUserItemVo getSystemUserById(Long id) {
        SystemUserItemVo itemVo = new SystemUserItemVo();

        SystemUser systemUser = systemUserMapper.selectById(id);

        SystemPost systemPost = systemPostMapper.selectById(id);

        BeanUtils.copyProperties(systemUser,itemVo);

        itemVo.setPostName(systemPost.getName());

        return itemVo;
    }
}




