package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.web.app.service.StoreLabelService;
import com.yourname.studyroom.web.app.mapper.AppStoreLabelMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【store_label(自习室门店标签关联表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class StoreLabelServiceImpl extends ServiceImpl<AppStoreLabelMapper, StoreLabel>
    implements StoreLabelService{
}
