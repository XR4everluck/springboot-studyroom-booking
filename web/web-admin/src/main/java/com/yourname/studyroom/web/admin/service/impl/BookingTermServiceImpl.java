package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.BookingTerm;
import com.yourname.studyroom.web.admin.service.BookingTermService;
import com.yourname.studyroom.web.admin.mapper.BookingTermMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【booking_term(预订时长)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class BookingTermServiceImpl extends ServiceImpl<BookingTermMapper, BookingTerm>
    implements BookingTermService{

}




