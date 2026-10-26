package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.BookingTerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【booking_term(预订时长)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.BookingTerm
*/
public interface AppBookingTermMapper extends BaseMapper<BookingTerm> {

    List<BookingTerm> selectListBySeatId(Long id);
}




