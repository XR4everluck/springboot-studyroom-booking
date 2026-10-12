package com.yourname.studyroom.web.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yourname.studyroom.model.entity.Seat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yourname.studyroom.web.app.vo.room.RoomDetailVo;
import com.yourname.studyroom.web.app.vo.room.RoomItemVo;
import com.yourname.studyroom.web.app.vo.room.RoomQueryVo;

/**
* @author liubo
* @description 针对表【seat(座位信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface SeatService extends IService<Seat> {
    IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    RoomDetailVo getDetailById(Long id);

    IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id);
}
