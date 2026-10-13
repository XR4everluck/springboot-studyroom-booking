package com.yourname.studyroom.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.model.entity.*;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.model.enums.SeatRealtimeStatus;
import com.yourname.studyroom.web.app.mapper.*;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.BrowsingHistoryService;
import com.yourname.studyroom.web.app.service.SeatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.web.app.vo.store.StoreItemVo;
import com.yourname.studyroom.web.app.vo.attr.AttrValueVo;
import com.yourname.studyroom.web.app.vo.fee.FeeValueVo;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import com.yourname.studyroom.web.app.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.app.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.app.vo.seat.SeatQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 针对表【seat(座位信息表)】的数据库操作Service实现
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
    BookingTermMapper bookingTermMapper;

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
    public IPage<SeatItemVo> pageItem(IPage<SeatItemVo> page, SeatQueryVo queryVo) {
        IPage<SeatItemVo> result = seatMapper.pageItem(page, queryVo);
        fillRealtimeStatus(result.getRecords());
        return result;
    }

    @Override
    public SeatDetailVo getDetailById(Long id) {
        //查询缓存
        String key = RedisConstant.APP_SEAT_PREFIX + id;
        SeatDetailVo seatDetailVo = (SeatDetailVo) redisTemplate.opsForValue().get(key);

        if (seatDetailVo == null){
            //1.查询座位信息
            Seat seat = seatMapper.selectById(id);
            if (seat == null) {
                return null;
            }
            //2.查询图片
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.SEAT, id);
            //3.查询预订时长
            List<BookingTerm> bookingTermList = bookingTermMapper.selectListBySeatId(id);
            //4.查询配套
            List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListBySeatId(id);
            //5.查询标签
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListBySeatId(id);
            //6.查询支付方式
            List<PaymentType> paymentTypeList = paymentTypeMapper.selectListBySeatId(id);
            //7.查询基本属性
            List<AttrValueVo> attrValueVoList = attrValueMapper.selectListBySeatId(id);
            //8.查询杂费信息
            List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByStoreId(seat.getStoreId());
            //9.查询自习室门店信息
            StoreItemVo storeItemVo = studyStoreService.selectStoreItemVoById(seat.getStoreId());

            seatDetailVo = new SeatDetailVo();
            BeanUtils.copyProperties(seat, seatDetailVo);

            seatDetailVo.setStoreItemVo(storeItemVo);
            seatDetailVo.setGraphVoList(graphVoList);
            seatDetailVo.setAttrValueVoList(attrValueVoList);
            seatDetailVo.setFacilityInfoList(facilityInfoList);
            seatDetailVo.setLabelInfoList(labelInfoList);
            seatDetailVo.setPaymentTypeList(paymentTypeList);
            seatDetailVo.setFeeValueVoList(feeValueVoList);
            seatDetailVo.setBookingTermList(bookingTermList);
            //加入缓存
            redisTemplate.opsForValue().set(key,seatDetailVo);
        }
        //异步 保存浏览历史，未登录游客浏览座位时不记录
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser != null) {
            browsingHistoryService.saveHistory(loginUser.getUserId(), id);
        }

        //实时状态基于 Redis，始终按当前缓存回填，避免受详情缓存快照影响
        SeatRealtimeStatus realtime = readRealtimeStatus(id);
        seatDetailVo.setRealtimeStatus(realtime.getCode());
        seatDetailVo.setRealtimeStatusName(realtime.getName());

        return seatDetailVo;
    }

    @Override
    public IPage<SeatItemVo> pageItemByStoreId(IPage<SeatItemVo> page, Long id) {
        IPage<SeatItemVo> result = seatMapper.pageItemByStoreId(page, id);
        fillRealtimeStatus(result.getRecords());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SeatItemVo> getHotList(int limit) {
        List<Long> hotIds = (List<Long>) redisTemplate.opsForValue().get(RedisConstant.APP_HOT_SEAT_LIST);
        if (hotIds == null) {
            hotIds = seatMapper.selectHotSeatIds(limit);
            redisTemplate.opsForValue().set(
                    RedisConstant.APP_HOT_SEAT_LIST,
                    new ArrayList<>(hotIds),
                    RedisConstant.APP_HOT_LIST_TTL_SEC,
                    TimeUnit.SECONDS);
        }
        List<SeatItemVo> result = new ArrayList<>();
        for (Long id : hotIds) {
            if (result.size() >= limit) {
                break;
            }
            SeatItemVo item = seatMapper.selectItemById(id);
            if (item != null) {
                result.add(item);
            }
        }
        fillRealtimeStatus(result);
        return result;
    }

    /**
     * 批量回填座位列表实时状态
     */
    private void fillRealtimeStatus(List<SeatItemVo> records) {
        if (records == null) {
            return;
        }
        for (SeatItemVo record : records) {
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




