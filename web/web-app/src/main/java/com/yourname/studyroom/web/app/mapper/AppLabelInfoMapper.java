package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.LabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【label_info(标签信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.LabelInfo
*/
public interface AppLabelInfoMapper extends BaseMapper<LabelInfo> {

    List<LabelInfo> selectListBySeatId(Long id);

    List<LabelInfo> selectListByStoreId(Long id);
}




