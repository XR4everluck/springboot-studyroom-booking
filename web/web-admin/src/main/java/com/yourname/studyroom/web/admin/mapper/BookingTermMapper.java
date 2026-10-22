package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.BookingTerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【booking_term(预订时长)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.BookingTerm
*/
public interface BookingTermMapper extends BaseMapper<BookingTerm> {

    List<BookingTerm> selectListBySeatId(Long id);
}




