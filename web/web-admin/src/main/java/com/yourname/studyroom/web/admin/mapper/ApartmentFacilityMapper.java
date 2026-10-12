package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.ApartmentFacility;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourname.studyroom.model.entity.SeatFacility;

import java.util.List;

/**
* @author liubo
* @description 针对表【apartment_facility(自习室门店&配套关联表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.ApartmentFacility
*/
public interface ApartmentFacilityMapper extends BaseMapper<ApartmentFacility> {

    List<SeatFacility> selectListByApartmentId(Long id);
}




