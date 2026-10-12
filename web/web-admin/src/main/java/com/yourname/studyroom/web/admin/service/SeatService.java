package com.yourname.studyroom.web.admin.service;

import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.web.admin.vo.room.RoomDetailVo;
import com.yourname.studyroom.web.admin.vo.room.RoomItemVo;
import com.yourname.studyroom.web.admin.vo.room.RoomQueryVo;
import com.yourname.studyroom.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【seat(座位信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface SeatService extends IService<Seat> {

    void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo);

    IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    RoomDetailVo getRoomDetailById(Long id);

    void removeRoomById(Long id);
}
