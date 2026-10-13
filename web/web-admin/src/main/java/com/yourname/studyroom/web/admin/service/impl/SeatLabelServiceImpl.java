package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.SeatLabel;
import com.yourname.studyroom.web.admin.service.SeatLabelService;
import com.yourname.studyroom.web.admin.mapper.SeatLabelMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【seat_label(座位&标签关联表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class SeatLabelServiceImpl extends ServiceImpl<SeatLabelMapper, SeatLabel>
    implements SeatLabelService{

}




