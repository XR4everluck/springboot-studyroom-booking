package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.model.entity.StudyStore;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.app.vo.store.StoreItemVo;

import java.util.List;

/**
 * 针对表【study_store(自习室门店信息表)】的数据库操作Service
 */
public interface StudyStoreService extends IService<StudyStore> {
    StoreItemVo selectStoreItemVoById(Long id);

    StoreDetailVo getDetailById(Long id);

    /**
     * 首页热门门店列表（带 Redis 缓存）
     */
    List<StoreItemVo> getHotList(int limit);
}
