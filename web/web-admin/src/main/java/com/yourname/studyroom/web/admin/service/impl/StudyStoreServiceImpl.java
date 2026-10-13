package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.admin.mapper.*;
import com.yourname.studyroom.web.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.admin.vo.store.StoreItemVo;
import com.yourname.studyroom.web.admin.vo.store.StoreQueryVo;
import com.yourname.studyroom.web.admin.vo.store.StoreSubmitVo;
import com.yourname.studyroom.web.admin.vo.fee.FeeValueVo;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author liubo
 * @description 针对表【study_store(自习室门店信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class StudyStoreServiceImpl extends ServiceImpl<StudyStoreMapper, StudyStore>
        implements StudyStoreService {

    @Autowired
    GraphInfoService graphInfoService;

    @Autowired
    StoreFacilityService storeFacilityService;

    @Autowired
    StoreLabelService storeLabelService;

    @Autowired
    StoreFeeValueService storeFeeValueService;

    @Autowired
    StudyStoreMapper studyStoreMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;

    @Autowired
    StoreFacilityMapper storeFacilityMapper;

    @Autowired
    FeeValueMapper feeValueMapper;

    @Autowired
    SeatMapper seatMapper;

    /**
     * 保存或更新自习室门店信息
     * <p>
     * 根据传入的自习室门店提交信息（StoreSubmitVo），保存或更新自习室门店的详细信息，包括图片、配套、标签和杂费信息。
     * 如果自习室门店ID不为空，则进行更新操作，并先删除原有的图片、配套、标签和杂费信息；
     * 如果自习室门店ID为空，则进行新增操作。
     *
     * @param storeSubmitVo 自习室门店提交信息对象，包含自习室门店的详细信息和相关列表信息
     */
    @Override
    public void saveOrUpdateStore(StoreSubmitVo storeSubmitVo) {
        boolean isUpdate = storeSubmitVo.getId() != null;
        super.saveOrUpdate(storeSubmitVo);

        if (isUpdate) {
            //1.删除图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.STORE);
            graphQueryWrapper.eq(GraphInfo::getItemId, storeSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);
            //2.删除配套列表
            LambdaQueryWrapper<StoreFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(StoreFacility::getStoreId, storeSubmitVo.getId());
            storeFacilityService.remove(facilityQueryWrapper);
            //3.删除标签列表
            LambdaQueryWrapper<StoreLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(StoreLabel::getStoreId, storeSubmitVo.getId());
            storeLabelService.remove(labelQueryWrapper);
            //4.删除杂费列表
            LambdaQueryWrapper<StoreFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
            feeValueQueryWrapper.eq(StoreFeeValue::getStoreId, storeSubmitVo.getId());
            storeFeeValueService.remove(feeValueQueryWrapper);
        }
        //1.插入图片列表
        List<GraphVo> graphVoList = storeSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.STORE);
                graphInfo.setItemId(storeSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }
        //2.插入配套列表
        List<Long> facilityInfoIdList = storeSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIdList)) {
            ArrayList<StoreFacility> storeFacilityList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIdList) {
                StoreFacility storeFacility = new StoreFacility();
                storeFacility.setStoreId(storeSubmitVo.getId());
                storeFacility.setFacilityId(facilityInfoId);
                storeFacilityList.add(storeFacility);
            }
            storeFacilityService.saveBatch(storeFacilityList);
        }
        //3.插入标签列表
        List<Long> labelIdList = storeSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelIdList)) {
            ArrayList<StoreLabel> storeLabelList = new ArrayList<>();
            for (Long lableId : labelIdList) {
                StoreLabel storeLabel = new StoreLabel();
                storeLabel.setStoreId(storeSubmitVo.getId());
                storeLabel.setLabelId(lableId);
                storeLabelList.add(storeLabel);
            }
            storeLabelService.saveBatch(storeLabelList);
        }
        //4.插入杂费列表
        List<Long> feeValueIds = storeSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<StoreFeeValue> storeFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                StoreFeeValue storeFeeValue = new StoreFeeValue();
                storeFeeValue.setStoreId(storeSubmitVo.getId());
                storeFeeValue.setFeeValueId(feeValueId);
                storeFeeValueList.add(storeFeeValue);
            }
            storeFeeValueService.saveBatch(storeFeeValueList);
        }
    }


    /**
     * 根据条件分页查询自习室门店列表
     *
     * @param page    分页对象，包含当前页码和每页记录数
     * @param queryVo 查询条件对象
     * @return 包含分页信息的自习室门店列表
     */
    @Override
    public IPage<StoreItemVo> pageItem(Page<StoreItemVo> page, StoreQueryVo queryVo) {
        return studyStoreMapper.pageItem(page, queryVo);
    }

    /**
     * 根据自习室门店ID获取自习室门店详细信息
     *
     * @param id 自习室门店ID
     * @return 包含自习室门店详细信息的StoreDetailVo对象
     */
    @Override
    public StoreDetailVo getDetailById(Long id) {
        //1.查询自习室门店信息
        StudyStore studyStore = studyStoreMapper.selectById(id);
        //2.查询配套列表
        List<GraphVo> graphVoList = graphInfoMapper.selectByItemTypeAndId(ItemType.STORE, id);
        //3.查询标签列表
        List<LabelInfo> labelInfoList = studyStoreMapper.selectListByStoreId(id);
        //4.查询配套列表
        List<FacilityInfo> facilityInfoList = storeFacilityMapper.selectListByStoreId(id);
        //5.查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByStoreId(id);
        //6.组装结果
        StoreDetailVo storeDetailVo = new StoreDetailVo();
        BeanUtils.copyProperties(studyStore, storeDetailVo);
        storeDetailVo.setGraphVoList(graphVoList);
        storeDetailVo.setLabelInfoList(labelInfoList);
        storeDetailVo.setFacilityInfoList(facilityInfoList);
        storeDetailVo.setFeeValueVoList(feeValueVoList);
        return storeDetailVo;
    }

    @Override
    public void removeStoreById(Long id) {
//      查询自习室门店下的座位数
        LambdaQueryWrapper<Seat> seatQueryWrapper = new LambdaQueryWrapper<>();
        seatQueryWrapper.eq(Seat::getStoreId, id);
        Long count = seatMapper.selectCount(seatQueryWrapper);
        if (count > 0) {
            //终止删除 并相应提示信息
            throw new BookingException(ResultCodeEnum.ADMIN_STORE_DELETE_ERROR);
        }
        super.removeById(id);
        //1.删除图片列表
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.STORE);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);
        //2.删除配套列表
        LambdaQueryWrapper<StoreFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(StoreFacility::getStoreId, id);
        storeFacilityService.remove(facilityQueryWrapper);
        //3.删除标签列表
        LambdaQueryWrapper<StoreLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(StoreLabel::getStoreId, id);
        storeLabelService.remove(labelQueryWrapper);
        //4.删除杂费列表
        LambdaQueryWrapper<StoreFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
        feeValueQueryWrapper.eq(StoreFeeValue::getStoreId, id);
        storeFeeValueService.remove(feeValueQueryWrapper);
    }
}




