package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentDetailVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentQueryVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【study_store(自习室门店信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface StudyStoreService extends IService<StudyStore> {

    void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo);

    IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo);

    ApartmentDetailVo getDetailById(Long id);

    void removeApartmentById(Long id);
}
