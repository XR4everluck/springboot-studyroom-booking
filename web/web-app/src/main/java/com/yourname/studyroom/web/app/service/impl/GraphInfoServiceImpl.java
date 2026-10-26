package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.GraphInfo;
import com.yourname.studyroom.web.app.service.GraphInfoService;
import com.yourname.studyroom.web.app.mapper.AppGraphInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【graph_info(图片信息表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class GraphInfoServiceImpl extends ServiceImpl<AppGraphInfoMapper, GraphInfo>
    implements GraphInfoService{

}




