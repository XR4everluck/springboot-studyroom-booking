package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.StoreFacility;
import com.yourname.studyroom.web.admin.service.StoreFacilityService;
import com.yourname.studyroom.web.admin.mapper.StoreFacilityMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【store_facility(自习室门店&配套关联表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class StoreFacilityServiceImpl extends ServiceImpl<StoreFacilityMapper, StoreFacility>
    implements StoreFacilityService{

}




