package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.web.admin.mapper.MemberCardMapper;
import com.yourname.studyroom.web.admin.service.MemberCardService;
import org.springframework.stereotype.Service;

/**
 * @description 针对表【member_card(会员卡信息表)】的数据库操作Service实现
 */
@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements MemberCardService {
}
