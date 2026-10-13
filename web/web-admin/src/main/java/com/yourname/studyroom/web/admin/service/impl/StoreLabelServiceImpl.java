package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.web.admin.service.StoreLabelService;
import com.yourname.studyroom.web.admin.mapper.StoreLabelMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【store_label(自习室门店标签关联表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class StoreLabelServiceImpl extends ServiceImpl<StoreLabelMapper, StoreLabel>
    implements StoreLabelService{

}




