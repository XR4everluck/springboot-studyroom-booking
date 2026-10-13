package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.SeatFacility;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentDetailVo;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【study_store(自习室门店信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class StudyStoreServiceImpl extends ServiceImpl<StudyStoreMapper, StudyStore>
        implements StudyStoreService {

    @Autowired
    StudyStoreMapper studyStoreMapper;

    @Autowired
    StoreLabelMapper storeLabelMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;

    @Autowired
    SeatMapper seatMapper;

    @Autowired
    SeatFacilityMapper seatFacilityMapper;

    @Override
    public ApartmentItemVo selectApartmentItemVoById(Long id) {
        StudyStore studyStore = studyStoreMapper.selectById(id);

        List<StoreLabel> labelInfoList = storeLabelMapper.selectListByApartmentId(id);

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        BigDecimal minRent = seatMapper.selectMinRentByApartmentId(id);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(studyStore, apartmentItemVo);

        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);
        return apartmentItemVo;
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //1.查询自习室门店信息
        StudyStore studyStore = studyStoreMapper.selectById(id);
        //2.查询图片信息
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);
        //3.查询标签信息
        List<StoreLabel> labelInfoList = storeLabelMapper.selectListByApartmentId(id);
        //4.查询配套信息
        List<SeatFacility> facilityInfoList = seatFacilityMapper.selectListByApartmentId(id);
        //5.查询最低座位单价
        BigDecimal minRent = seatMapper.selectMinRentByApartmentId(id);

        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();

        BeanUtils.copyProperties(studyStore, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setMinRent(minRent);
        return apartmentDetailVo;
    }
}




