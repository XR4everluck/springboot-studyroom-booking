package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.web.app.service.UserInfoService;
import com.yourname.studyroom.web.app.mapper.AppUserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<AppUserInfoMapper, UserInfo>
    implements UserInfoService{

}




