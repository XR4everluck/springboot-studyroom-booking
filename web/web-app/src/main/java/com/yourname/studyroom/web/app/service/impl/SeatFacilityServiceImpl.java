package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.SeatFacility;
import com.yourname.studyroom.web.app.service.SeatFacilityService;
import com.yourname.studyroom.web.app.mapper.SeatFacilityMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【facility_info(配套信息表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class SeatFacilityServiceImpl extends ServiceImpl<SeatFacilityMapper, SeatFacility>
    implements SeatFacilityService{

}




