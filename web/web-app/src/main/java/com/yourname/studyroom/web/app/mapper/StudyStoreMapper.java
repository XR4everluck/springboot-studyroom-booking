package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.StudyStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表【study_store(自习室门店信息表)】的数据库操作Mapper
 */
public interface StudyStoreMapper extends BaseMapper<StudyStore> {

    /**
     * 按浏览量降序返回已上架门店 id（首页热门门店）
     */
    @Select("select ai.id " +
            "from study_store ai " +
            "join seat ri on ri.store_id = ai.id and ri.is_deleted = 0 " +
            "join browsing_history bh on bh.seat_id = ri.id and bh.is_deleted = 0 " +
            "where ai.is_deleted = 0 and ai.is_release = 1 " +
            "group by ai.id " +
            "order by count(bh.id) desc " +
            "limit #{limit}")
    List<Long> selectHotStoreIds(Integer limit);
}




