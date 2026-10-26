package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.StoreFeeValue;
import com.yourname.studyroom.web.app.service.StoreFeeValueService;
import com.yourname.studyroom.web.app.mapper.AppStoreFeeValueMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【store_fee_value(自习室门店&杂费关联表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class StoreFeeValueServiceImpl extends ServiceImpl<AppStoreFeeValueMapper, StoreFeeValue>
    implements StoreFeeValueService{
}




