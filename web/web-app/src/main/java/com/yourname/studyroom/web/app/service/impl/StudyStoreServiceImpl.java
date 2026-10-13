package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.FacilityInfo;
import com.yourname.studyroom.model.entity.LabelInfo;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.app.vo.store.StoreItemVo;
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
    LabelInfoMapper labelInfoMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;

    @Autowired
    SeatMapper seatMapper;

    @Autowired
    FacilityInfoMapper facilityInfoMapper;

    @Override
    public StoreItemVo selectStoreItemVoById(Long id) {
        StudyStore studyStore = studyStoreMapper.selectById(id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByStoreId(id);

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.STORE, id);

        BigDecimal minPrice = seatMapper.selectMinPriceByStoreId(id);

        StoreItemVo storeItemVo = new StoreItemVo();
        BeanUtils.copyProperties(studyStore, storeItemVo);

        storeItemVo.setGraphVoList(graphVoList);
        storeItemVo.setLabelInfoList(labelInfoList);
        storeItemVo.setMinPrice(minPrice);
        return storeItemVo;
    }

    @Override
    public StoreDetailVo getDetailById(Long id) {
        //1.查询自习室门店信息
        StudyStore studyStore = studyStoreMapper.selectById(id);
        //2.查询图片信息
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.STORE, id);
        //3.查询标签信息
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByStoreId(id);
        //4.查询配套信息
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByStoreId(id);
        //5.查询最低座位单价
        BigDecimal minPrice = seatMapper.selectMinPriceByStoreId(id);

        StoreDetailVo storeDetailVo = new StoreDetailVo();

        BeanUtils.copyProperties(studyStore, storeDetailVo);
        storeDetailVo.setGraphVoList(graphVoList);
        storeDetailVo.setLabelInfoList(labelInfoList);
        storeDetailVo.setFacilityInfoList(facilityInfoList);
        storeDetailVo.setMinPrice(minPrice);
        return storeDetailVo;
    }
}




