package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.StoreFacility;
import com.yourname.studyroom.web.app.service.StoreFacilityService;
import com.yourname.studyroom.web.app.mapper.AppStoreFacilityMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【store_facility(自习室门店&配套关联表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class StoreFacilityServiceImpl extends ServiceImpl<AppStoreFacilityMapper, StoreFacility>
    implements StoreFacilityService{
}




