package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.SeatFacility;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【facility_info(配套信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.SeatFacility
*/
public interface SeatFacilityMapper extends BaseMapper<SeatFacility> {

    List<SeatFacility> selectListByRoomId(Long id);

    List<SeatFacility> selectListByApartmentId(Long id);
}




