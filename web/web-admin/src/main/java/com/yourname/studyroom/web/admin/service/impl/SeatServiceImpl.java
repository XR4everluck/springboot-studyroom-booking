package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.admin.mapper.*;
import com.yourname.studyroom.web.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.admin.vo.attr.AttrValueVo;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatQueryVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatSubmitVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【seat(座位信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat>
        implements SeatService {

    @Autowired
    GraphInfoService graphInfoService;

    @Autowired
    SeatAttrValueService seatAttrValueService;

    @Autowired
    SeatFacilityService seatFacilityService;

    @Autowired
    SeatLabelService seatLabelService;

    @Autowired
    SeatPaymentTypeService seatPaymentTypeService;

    @Autowired
    SeatBookingTermService seatBookingTermService;

    @Autowired
    SeatMapper seatMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;
    @Autowired
    StudyStoreMapper studyStoreMapper;

    @Autowired
    AttrValueMapper attrValueMapper;

    @Autowired
    FacilityInfoMapper facilityInfoMapper;

    @Autowired
    LabelInfoMapper labelInfoMapper;

    @Autowired
    PaymentTypeMapper paymentTypeMapper;

    @Autowired
    BookingTermMapper bookingTermMapper;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public void saveOrUpdateSeat(SeatSubmitVo seatSubmitVo) {
        boolean isUpdate = seatSubmitVo.getId() != null;
        super.saveOrUpdate(seatSubmitVo);

        //若为更新操作，则先删除与Seat相关的各项信息列表
        if (isUpdate) {
            //1.删除原有graphInfoList
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.SEAT);
            graphQueryWrapper.eq(GraphInfo::getItemId, seatSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);

            //2.删除原有seatAttrValueList
            LambdaQueryWrapper<SeatAttrValue> attrQueryMapper = new LambdaQueryWrapper<>();
            attrQueryMapper.eq(SeatAttrValue::getSeatId, seatSubmitVo.getId());
            seatAttrValueService.remove(attrQueryMapper);

            //3.删除原有seatFacilityList
            LambdaQueryWrapper<SeatFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(SeatFacility::getSeatId, seatSubmitVo.getId());
            seatFacilityService.remove(facilityQueryWrapper);

            //4.删除原有seatLabelList
            LambdaQueryWrapper<SeatLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(SeatLabel::getSeatId, seatSubmitVo.getId());
            seatLabelService.remove(labelQueryWrapper);

            //5.删除原有paymentTypeList
            LambdaQueryWrapper<SeatPaymentType> paymentQueryWrapper = new LambdaQueryWrapper<>();
            paymentQueryWrapper.eq(SeatPaymentType::getSeatId, seatSubmitVo.getId());
            seatPaymentTypeService.remove(paymentQueryWrapper);


            //6.删除原有bookingTermList
            LambdaQueryWrapper<SeatBookingTerm> termQueryWrapper = new LambdaQueryWrapper<>();
            termQueryWrapper.eq(SeatBookingTerm::getSeatId, seatSubmitVo.getId());
            seatBookingTermService.remove(termQueryWrapper);

            //删除缓存
            String key = RedisConstant.APP_SEAT_PREFIX+seatSubmitVo.getId();
            redisTemplate.delete(key);
        }

        //1.保存新的graphInfoList
        List<GraphVo> graphVoList = seatSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.SEAT);
                graphInfo.setItemId(seatSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }

        //2.保存新的seatAttrValueList
        List<Long> attrValueIds = seatSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            List<SeatAttrValue> seatAttrValueList = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                SeatAttrValue seatAttrValue = SeatAttrValue.builder().seatId(seatSubmitVo.getId()).attrValueId(attrValueId).build();
                seatAttrValueList.add(seatAttrValue);
            }
            seatAttrValueService.saveBatch(seatAttrValueList);
        }

        //3.保存新的facilityInfoList
        List<Long> facilityInfoIds = seatSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            List<SeatFacility> seatFacilityList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                SeatFacility seatFacility = SeatFacility.builder().seatId(seatSubmitVo.getId()).facilityId(facilityInfoId).build();
                seatFacilityList.add(seatFacility);
            }
            seatFacilityService.saveBatch(seatFacilityList);
        }

        //4.保存新的labelInfoList
        List<Long> labelInfoIds = seatSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelInfoIds)) {
            ArrayList<SeatLabel> seatLabelList = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                SeatLabel seatLabel = SeatLabel.builder().seatId(seatSubmitVo.getId()).labelId(labelInfoId).build();
                seatLabelList.add(seatLabel);
            }
            seatLabelService.saveBatch(seatLabelList);
        }

        //5.保存新的paymentTypeList
        List<Long> paymentTypeIds = seatSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(paymentTypeIds)) {
            ArrayList<SeatPaymentType> seatPaymentTypeList = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                SeatPaymentType seatPaymentType = SeatPaymentType.builder().seatId(seatSubmitVo.getId()).paymentTypeId(paymentTypeId).build();
                seatPaymentTypeList.add(seatPaymentType);
            }
            seatPaymentTypeService.saveBatch(seatPaymentTypeList);
        }

        //6.保存新的bookingTermList
        List<Long> bookingTermIds = seatSubmitVo.getBookingTermIds();
        if (!CollectionUtils.isEmpty(bookingTermIds)) {
            ArrayList<SeatBookingTerm> seatBookingTerms = new ArrayList<>();
            for (Long bookingTermId : bookingTermIds) {
                SeatBookingTerm seatBookingTerm = SeatBookingTerm.builder().seatId(seatSubmitVo.getId()).bookingTermId(bookingTermId).build();
                seatBookingTerms.add(seatBookingTerm);
            }
            seatBookingTermService.saveBatch(seatBookingTerms);
        }
    }

    @Override
    public IPage<SeatItemVo> pageSeatItemByQuery(IPage<SeatItemVo> page, SeatQueryVo queryVo) {
        return seatMapper.pageSeatItemByQuery(page, queryVo);
    }

    @Override
    public SeatDetailVo getSeatDetailById(Long id) {
        //1.查询Seat
        Seat seat = seatMapper.selectById(id);

        //2.查询所属自习室门店信息
        StudyStore studyStore = studyStoreMapper.selectById(seat.getStoreId());

        //3.查询graphInfoList
        List<GraphVo> graphVoList = graphInfoMapper.selectByItemTypeAndId(ItemType.SEAT, id);

        //4.查询attrValueList
        List<AttrValueVo> attrvalueVoList = attrValueMapper.selectListBySeatId(id);

        //5.查询facilityInfoList
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListBySeatId(id);

        //6.查询labelInfoList
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListBySeatId(id);

        //7.查询paymentTypeList
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListBySeatId(id);

        //8.查询bookingTermList
        List<BookingTerm> bookingTermList = bookingTermMapper.selectListBySeatId(id);


        SeatDetailVo adminSeatDetailVo = new SeatDetailVo();
        BeanUtils.copyProperties(seat, adminSeatDetailVo);

        adminSeatDetailVo.setStudyStore(studyStore);
        adminSeatDetailVo.setGraphVoList(graphVoList);
        adminSeatDetailVo.setAttrValueVoList(attrvalueVoList);
        adminSeatDetailVo.setFacilityInfoList(facilityInfoList);
        adminSeatDetailVo.setLabelInfoList(labelInfoList);
        adminSeatDetailVo.setPaymentTypeList(paymentTypeList);
        adminSeatDetailVo.setBookingTermList(bookingTermList);

        return adminSeatDetailVo;
    }

    @Override
    public void removeSeatById(Long id) {
        //1.删除Seat
        super.removeById(id);

        //2.删除graphInfoList
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.SEAT);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //3.删除attrValueList
        LambdaQueryWrapper<SeatAttrValue> attrQueryWrapper = new LambdaQueryWrapper<>();
        attrQueryWrapper.eq(SeatAttrValue::getSeatId, id);
        seatAttrValueService.remove(attrQueryWrapper);

        //4.删除facilityInfoList
        LambdaQueryWrapper<SeatFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(SeatFacility::getSeatId, id);
        seatFacilityService.remove(facilityQueryWrapper);

        //5.删除labelInfoList
        LambdaQueryWrapper<SeatLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(SeatLabel::getSeatId, id);
        seatLabelService.remove(labelQueryWrapper);

        //6.删除paymentTypeList
        LambdaQueryWrapper<SeatPaymentType> paymentQueryWrapper = new LambdaQueryWrapper<>();
        paymentQueryWrapper.eq(SeatPaymentType::getSeatId, id);
        seatPaymentTypeService.remove(paymentQueryWrapper);

        //7.删除bookingTermList
        LambdaQueryWrapper<SeatBookingTerm> termQueryWrapper = new LambdaQueryWrapper<>();
        termQueryWrapper.eq(SeatBookingTerm::getSeatId, id);
        seatBookingTermService.remove(termQueryWrapper);

        //删除缓存
        String key = RedisConstant.APP_SEAT_PREFIX + id;
        redisTemplate.delete(key);
    }
}




