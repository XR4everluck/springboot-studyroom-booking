package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.RoomLeaseTerm;
import com.yourname.studyroom.web.admin.service.RoomLeaseTermService;
import com.yourname.studyroom.web.admin.mapper.RoomLeaseTermMapper;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【room_lease_term(座位租期管理表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class RoomLeaseTermServiceImpl extends ServiceImpl<RoomLeaseTermMapper, RoomLeaseTerm>
    implements RoomLeaseTermService{

}




