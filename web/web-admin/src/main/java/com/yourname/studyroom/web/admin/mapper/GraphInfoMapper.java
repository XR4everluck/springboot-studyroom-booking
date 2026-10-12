package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.GraphInfo;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【graph_info(图片信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.GraphInfo
*/
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {

    List<GraphVo> selectByItemTypeAndId(ItemType itemType, Long id);

}




