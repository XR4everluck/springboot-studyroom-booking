package com.yourname.studyroom.web.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.model.entity.Seat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.app.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.app.vo.seat.SeatQueryVo;

import java.util.List;

/**
 * 针对表【seat(座位信息表)】的数据库操作Service
 */
public interface SeatService extends IService<Seat> {
    IPage<SeatItemVo> pageItem(IPage<SeatItemVo> page, SeatQueryVo queryVo);

    SeatDetailVo getDetailById(Long id);

    IPage<SeatItemVo> pageItemByStoreId(IPage<SeatItemVo> page, Long id);

    /**
     * 首页热门座位列表（带 Redis 缓存）
     */
    List<SeatItemVo> getHotList(int limit);
}
