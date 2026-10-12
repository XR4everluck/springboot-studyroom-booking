package com.yourname.studyroom.web.app.service;

import com.yourname.studyroom.model.entity.ApartmentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentDetailVo;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;

/**
 * @author liubo
 * @description 针对表【apartment_info(自习室门店信息表)】的数据库操作Service
 * @createDate 2023-07-26 11:12:39
 */
public interface ApartmentInfoService extends IService<ApartmentInfo> {
    ApartmentItemVo selectApartmentItemVoById(Long id);

    ApartmentDetailVo getDetailById(Long id);
}
