package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.ApartmentInfo;
import com.yourname.studyroom.model.entity.LabelInfo;
import com.yourname.studyroom.model.enums.LeaseStatus;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
* @author liubo
* @description 针对表【apartment_info(自习室门店信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.ApartmentInfo
*/
public interface ApartmentInfoMapper extends BaseMapper<ApartmentInfo> {

    IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo);

    List<LabelInfo> selectListByApartmentId(Long id);
}




