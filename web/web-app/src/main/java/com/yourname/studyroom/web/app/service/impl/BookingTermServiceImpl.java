package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.model.entity.BookingTerm;
import com.yourname.studyroom.web.app.mapper.AppBookingTermMapper;
import com.yourname.studyroom.web.app.service.BookingTermService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【booking_term(预订时长)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BookingTermServiceImpl extends ServiceImpl<AppBookingTermMapper, BookingTerm>
        implements BookingTermService {

    @Autowired
    AppBookingTermMapper bookingTermMapper;

    @Override
    public List<BookingTerm> listBySeatId(Long id) {
        return bookingTermMapper.selectListBySeatId(id);
    }
}




