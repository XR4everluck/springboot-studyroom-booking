package com.yourname.studyroom.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.web.admin.service.UserInfoService;
import com.yourname.studyroom.web.admin.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
* 用户信息业务实现
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final SecureRandom TEMP_PWD_RANDOM = new SecureRandom();
    // 临时密码字符集：剔除易混淆字符（0/O、1/l/I）
    private static final String TEMP_PWD_LETTERS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private static final String TEMP_PWD_DIGITS = "23456789";

    /**
     * 管理员重置 C 端用户密码：生成 10 位临时密码（含字母和数字，满足密码规则），
     * BCrypt 加盐落库并置强制改密标记；与用户端自助改密一致地记录改密时间，
     * 使重置前签发的所有令牌立即失效
     *
     * @param id 用户ID
     * @return 临时明文密码（仅本次响应返回，由管理员当场告知用户）
     */
    @Override
    public String resetPasswordById(Long id) {
        UserInfo userInfo = getById(id);
        if (userInfo == null) {
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        String tempPassword = generateTempPassword();
        UserInfo update = new UserInfo();
        update.setId(id);
        update.setPassword(PasswordUtil.encode(tempPassword));
        update.setMustChangePwd(1);
        updateById(update);
        // 记录改密时间，与用户端自助改密共用同一失效机制
        redisTemplate.opsForValue().set(
                RedisConstant.APP_PASSWORD_CHANGED_PREFIX + id,
                String.valueOf(System.currentTimeMillis() / 1000),
                RedisConstant.APP_PASSWORD_CHANGED_TTL_SEC, TimeUnit.SECONDS);
        return tempPassword;
    }

    /**
     * 生成临时密码：1 位字母 + 1 位数字 + 8 位随机字符，保证满足"含字母和数字"规则
     */
    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(TEMP_PWD_LETTERS.charAt(TEMP_PWD_RANDOM.nextInt(TEMP_PWD_LETTERS.length())));
        sb.append(TEMP_PWD_DIGITS.charAt(TEMP_PWD_RANDOM.nextInt(TEMP_PWD_DIGITS.length())));
        String all = TEMP_PWD_LETTERS + TEMP_PWD_DIGITS;
        while (sb.length() < 10) {
            sb.append(all.charAt(TEMP_PWD_RANDOM.nextInt(all.length())));
        }
        return sb.toString();
    }
}
