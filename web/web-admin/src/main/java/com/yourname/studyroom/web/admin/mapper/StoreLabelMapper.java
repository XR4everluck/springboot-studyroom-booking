package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.model.enums.ItemType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【label_info(标签信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.StoreLabel
*/
public interface StoreLabelMapper extends BaseMapper<StoreLabel> {

    List<StoreLabel> selectListByRoomId(Long id);
}




