package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.SeatAttrValue;
import com.yourname.studyroom.web.admin.service.SeatAttrValueService;
import com.yourname.studyroom.web.admin.mapper.SeatAttrValueMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【seat_attr_value(座位&基本属性值关联表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class SeatAttrValueServiceImpl extends ServiceImpl<SeatAttrValueMapper, SeatAttrValue>
    implements SeatAttrValueService{

}




