package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.model.enums.SeatRealtimeStatus;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.BrowsingHistoryService;
import com.yourname.studyroom.web.app.service.SeatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.app.vo.attr.AttrValueVo;
import com.yourname.studyroom.web.app.vo.fee.FeeValueVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import com.yourname.studyroom.web.app.vo.room.RoomDetailVo;
import com.yourname.studyroom.web.app.vo.room.RoomItemVo;
import com.yourname.studyroom.web.app.vo.room.RoomQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【seat(座位信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat>
        implements SeatService {

    @Autowired
    SeatMapper seatMapper;

    @Autowired
    GraphInfoMapper graphInfoMapper;

    @Autowired
    LeaseTermMapper leaseTermMapper;

    @Autowired
    FacilityInfoMapper facilityInfoMapper;

    @Autowired
    LabelInfoMapper labelInfoMapper;

    @Autowired
    PaymentTypeMapper paymentTypeMapper;

    @Autowired
    AttrValueMapper attrValueMapper;

    @Autowired
    FeeValueMapper feeValueMapper;

    @Autowired
    StudyStoreService studyStoreService;

    @Autowired
    BrowsingHistoryService browsingHistoryService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        IPage<RoomItemVo> result = seatMapper.pageItem(page, queryVo);
        fillRealtimeStatus(result.getRecords());
        return result;
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        //查询缓存
        String key = RedisConstant.APP_ROOM_PREFIX + id;
        RoomDetailVo roomDetailVo = (RoomDetailVo) redisTemplate.opsForValue().get(key);

        if (roomDetailVo == null){
            //1.查询座位信息
            Seat seat = seatMapper.selectById(id);
            if (seat == null) {
                return null;
            }
            //2.查询图片
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);
            //3.查询租期
            List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);
            //4.查询配套
            List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);
            //5.查询标签
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);
            //6.查询支付方式
            List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);
            //7.查询基本属性
            List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);
            //8.查询杂费信息
            List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(seat.getStoreId());
            //9.查询自习室门店信息
            ApartmentItemVo storeItemVo = studyStoreService.selectApartmentItemVoById(seat.getStoreId());

            roomDetailVo = new RoomDetailVo();
            BeanUtils.copyProperties(seat, roomDetailVo);

            roomDetailVo.setStoreItemVo(storeItemVo);
            roomDetailVo.setGraphVoList(graphVoList);
            roomDetailVo.setAttrValueVoList(attrValueVoList);
            roomDetailVo.setFacilityInfoList(facilityInfoList);
            roomDetailVo.setLabelInfoList(labelInfoList);
            roomDetailVo.setPaymentTypeList(paymentTypeList);
            roomDetailVo.setFeeValueVoList(feeValueVoList);
            roomDetailVo.setLeaseTermList(leaseTermList);
            //加入缓存
            redisTemplate.opsForValue().set(key,roomDetailVo);
        }
        //异步 保存浏览历史
        browsingHistoryService.saveHistory(LoginUserHolder.getLoginUser().getUserId(),id);

        //实时状态基于 Redis，始终按当前缓存回填，避免受详情缓存快照影响
        SeatRealtimeStatus realtime = readRealtimeStatus(id);
        roomDetailVo.setRealtimeStatus(realtime.getCode());
        roomDetailVo.setRealtimeStatusName(realtime.getName());

        return roomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id) {
        IPage<RoomItemVo> result = seatMapper.pageItemByApartmentId(page, id);
        fillRealtimeStatus(result.getRecords());
        return result;
    }

    /**
     * 批量回填座位列表实时状态
     */
    private void fillRealtimeStatus(List<RoomItemVo> records) {
        if (records == null) {
            return;
        }
        for (RoomItemVo record : records) {
            SeatRealtimeStatus realtime = readRealtimeStatus(record.getId());
            record.setRealtimeStatus(realtime.getCode());
            record.setRealtimeStatusName(realtime.getName());
        }
    }

    /**
     * 从 Redis 读取座位实时状态，缺省视为空闲
     */
    private SeatRealtimeStatus readRealtimeStatus(Long seatId) {
        Object cached = redisTemplate.opsForValue().get(RedisConstant.APP_SEAT_STATUS_PREFIX + seatId);
        if (cached instanceof Number) {
            return SeatRealtimeStatus.ofCode(((Number) cached).intValue());
        }
        return SeatRealtimeStatus.FREE;
    }
}




