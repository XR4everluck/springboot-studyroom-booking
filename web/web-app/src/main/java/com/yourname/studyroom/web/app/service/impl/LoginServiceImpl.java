package com.yourname.studyroom.web.app.service.impl;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.web.app.mapper.UserInfoMapper;
import com.yourname.studyroom.web.app.service.LoginService;
import com.yourname.studyroom.web.app.vo.user.LoginVo;
import com.yourname.studyroom.web.app.vo.user.RegisterVo;
import com.yourname.studyroom.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 防账号枚举：账号不存在时也执行一次同代价的哈希校验，抹平响应时间差
     */
    private static final String DUMMY_HASH = PasswordUtil.encode("dummy-password-for-timing");

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$";
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 账号密码登录：失败计数锁定 → 密码校验（防枚举）→ 状态检查
     *
     * @param loginVo 登录信息对象，包含账号与密码
     * @return 登录成功后返回的JWT令牌
     */
    @Override
    public String login(LoginVo loginVo) {
        String username = trimToNull(loginVo.getUsername());
        if (username == null) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_USERNAME_EMPTY);
        }
        if (isBlank(loginVo.getPassword())) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_PASSWORD_EMPTY);
        }

        // 连续失败达到上限后锁定，防止暴力破解
        String failKey = RedisConstant.APP_LOGIN_FAIL_PREFIX + username;
        String failCount = redisTemplate.opsForValue().get(failKey);
        if (failCount != null && Integer.parseInt(failCount) >= RedisConstant.APP_LOGIN_FAIL_MAX) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
        }

        UserInfo userInfo = userInfoMapper.selectByUsername(username);
        // 账号不存在与密码错误统一提示，不暴露账号是否已注册
        if (!PasswordUtil.matches(loginVo.getPassword(),
                userInfo == null ? DUMMY_HASH : userInfo.getPassword())) {
            recordLoginFailure(failKey);
            throw new BookingException(ResultCodeEnum.APP_LOGIN_ERROR);
        }
        if (userInfo.getStatus() == BaseStatus.DISABLE) {
            throw new BookingException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        redisTemplate.delete(failKey);
        // token 的 userName claim 沿用手机号，作为订单联系方式
        return JwtUtil.createToken(userInfo.getId(), userInfo.getPhone() == null ? "" : userInfo.getPhone());
    }

    private void recordLoginFailure(String failKey) {
        redisTemplate.opsForValue().increment(failKey);
        redisTemplate.expire(failKey, RedisConstant.APP_LOGIN_FAIL_TTL_SEC, TimeUnit.SECONDS);
    }

    /**
     * 账号注册：与前端一致的校验规则，密码加盐哈希后存储
     *
     * @param registerVo 注册信息对象，包含账号、密码、昵称与选填手机号
     */
    @Override
    public void register(RegisterVo registerVo) {
        String username = trimToNull(registerVo.getUsername());
        if (username == null || !username.matches(USERNAME_PATTERN)) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_USERNAME_INVALID);
        }
        if (registerVo.getPassword() == null || !registerVo.getPassword().matches(PASSWORD_PATTERN)) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_PASSWORD_INVALID);
        }
        String nickname = trimToNull(registerVo.getNickname());
        if (nickname == null || nickname.length() > 12) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_NICKNAME_INVALID);
        }
        String phone = trimToNull(registerVo.getPhone());
        if (phone != null && !phone.matches(PHONE_PATTERN)) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_PHONE_INVALID);
        }

        if (userInfoMapper.selectByUsername(username) != null) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_USERNAME_EXISTS);
        }
        if (phone != null && userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, phone)) > 0) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_PHONE_EXISTS);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(PasswordUtil.encode(registerVo.getPassword()));
        userInfo.setNickname(nickname);
        userInfo.setPhone(phone);
        userInfo.setStatus(BaseStatus.ENABLE);
        userInfoMapper.insert(userInfo);
    }

    /**
     * 根据用户ID获取登录用户信息
     *
     * @param userId 用户ID
     * @return 登录用户信息对象
     */
    @Override
    public UserInfoVo getLogInUserById(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        UserInfoVo userInfoVo = new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
        return userInfoVo;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
