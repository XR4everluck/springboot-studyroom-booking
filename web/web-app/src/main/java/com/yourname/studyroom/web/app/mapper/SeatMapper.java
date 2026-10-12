package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.web.app.vo.room.RoomItemVo;
import com.yourname.studyroom.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;

/**
* @author liubo
* @description 针对表【seat(座位信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.Seat
*/
public interface SeatMapper extends BaseMapper<Seat> {

    IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    BigDecimal selectMinRentByApartmentId(Long id);

    IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id);
}