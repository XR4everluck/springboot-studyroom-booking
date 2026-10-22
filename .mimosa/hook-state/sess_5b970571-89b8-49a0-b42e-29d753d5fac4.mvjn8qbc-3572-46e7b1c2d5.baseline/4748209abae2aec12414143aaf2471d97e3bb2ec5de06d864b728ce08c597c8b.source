package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.web.admin.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.admin.vo.store.StoreItemVo;
import com.yourname.studyroom.web.admin.vo.store.StoreQueryVo;
import com.yourname.studyroom.web.admin.vo.store.StoreSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【study_store(自习室门店信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface StudyStoreService extends IService<StudyStore> {

    void saveOrUpdateStore(StoreSubmitVo storeSubmitVo);

    IPage<StoreItemVo> pageItem(Page<StoreItemVo> page, StoreQueryVo queryVo);

    StoreDetailVo getDetailById(Long id);

    void removeStoreById(Long id);
}
