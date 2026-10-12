package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.web.admin.vo.room.RoomItemVo;
import com.yourname.studyroom.web.admin.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author liubo
* @description 针对表【seat(座位信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.Seat
*/
public interface SeatMapper extends BaseMapper<Seat> {

    IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo);
}




