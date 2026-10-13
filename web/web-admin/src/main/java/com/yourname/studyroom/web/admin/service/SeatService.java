package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.web.admin.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatQueryVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【seat(座位信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface SeatService extends IService<Seat> {

    void saveOrUpdateSeat(SeatSubmitVo seatSubmitVo);

    IPage<SeatItemVo> pageSeatItemByQuery(IPage<SeatItemVo> page, SeatQueryVo queryVo);

    SeatDetailVo getSeatDetailById(Long id);

    void removeSeatById(Long id);
}
