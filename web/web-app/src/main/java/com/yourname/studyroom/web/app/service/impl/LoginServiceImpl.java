package com.yourname.studyroom.web.app.service.impl;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.web.app.mapper.AppUserInfoMapper;
import com.yourname.studyroom.web.app.service.LoginService;
import com.yourname.studyroom.web.app.vo.user.ChangePasswordVo;
import com.yourname.studyroom.web.app.vo.user.LoginVo;
import com.yourname.studyroom.web.app.vo.user.RegisterVo;
import com.yourname.studyroom.web.app.vo.user.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AppUserInfoMapper userInfoMapper;

    /**
     * 防账号枚举：账号不存在时也执行一次同代价的哈希校验，抹平响应时间差
     */
    private static final String DUMMY_HASH = PasswordUtil.encode("dummy-password-for-timing");

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$";

    /**
     * 账号密码登录：失败计数锁定 → 密码校验（防枚举）→ 状态检查 → 存量旧哈希渐进升级
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
        if (getFailCount(failKey) >= RedisConstant.APP_LOGIN_FAIL_MAX) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
        }

        UserInfo userInfo = userInfoMapper.selectByUsername(username);
        // 账号不存在与密码错误统一提示，不暴露账号是否已注册
        if (!PasswordUtil.matches(loginVo.getPassword(),
                userInfo == null ? DUMMY_HASH : userInfo.getPassword())) {
            long remain = recordLoginFailure(failKey);
            if (remain <= 0) {
                throw new BookingException(ResultCodeEnum.APP_LOGIN_LOCKED);
            }
            // 提示剩余可尝试次数，比笼统的"账号或密码错误"更贴近用户习惯
            throw new BookingException(ResultCodeEnum.APP_LOGIN_ERROR.getCode(),
                    "账号或密码错误，还可尝试 " + remain + " 次");
        }
        if (userInfo.getStatus() == BaseStatus.DISABLE) {
            throw new BookingException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        // 存量 PBKDF2 哈希在登录成功后用 BCrypt 重编码落库，渐进升级、存量用户无感知
        if (PasswordUtil.needsRehash(userInfo.getPassword())) {
            UserInfo upgrade = new UserInfo();
            upgrade.setId(userInfo.getId());
            upgrade.setPassword(PasswordUtil.encode(loginVo.getPassword()));
            userInfoMapper.updateById(upgrade);
        }

        redisTemplate.delete(failKey);
        // token 的 userName claim 沿用手机号，作为订单联系方式
        return JwtUtil.createToken(userInfo.getId(), userInfo.getPhone() == null ? "" : userInfo.getPhone(),
                LoginUserType.APP);
    }

    /**
     * 读取当前累计失败次数，无记录视为 0
     */
    private long getFailCount(String failKey) {
        String value = redisTemplate.opsForValue().get(failKey);
        return value == null ? 0L : Long.parseLong(value);
    }

    /**
     * 记录一次登录失败，并返回剩余可尝试次数
     */
    private long recordLoginFailure(String failKey) {
        Long count = redisTemplate.opsForValue().increment(failKey);
        redisTemplate.expire(failKey, RedisConstant.APP_LOGIN_FAIL_TTL_SEC, TimeUnit.SECONDS);
        long current = count == null ? 1L : count;
        return RedisConstant.APP_LOGIN_FAIL_MAX - current;
    }

    /**
     * 账号注册：与前端一致的校验规则，密码加盐哈希后存储；
     * 昵称不再单独收集，未传时默认使用登录账号
     *
     * @param registerVo 注册信息对象，包含账号与密码
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
        if (nickname != null && nickname.length() > 12) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_NICKNAME_INVALID);
        }

        if (userInfoMapper.selectByUsername(username) != null) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_USERNAME_EXISTS);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(PasswordUtil.encode(registerVo.getPassword()));
        userInfo.setNickname(nickname == null ? username : nickname);
        userInfo.setStatus(BaseStatus.ENABLE);
        userInfoMapper.insert(userInfo);
    }

    /**
     * 修改密码：校验原密码 → 校验新密码强度与一致性 → 落库（BCrypt 加盐哈希）
     *
     * @param userId           当前登录用户ID
     * @param changePasswordVo 修改密码信息对象
     */
    @Override
    public void changePassword(Long userId, ChangePasswordVo changePasswordVo) {
        if (userId == null) {
            throw new BookingException(ResultCodeEnum.APP_PASSWORD_NOT_LOGIN);
        }
        if (isBlank(changePasswordVo.getOldPassword())) {
            throw new BookingException(ResultCodeEnum.APP_LOGIN_PASSWORD_EMPTY);
        }
        String newPassword = changePasswordVo.getNewPassword();
        if (newPassword == null || !newPassword.matches(PASSWORD_PATTERN)) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_PASSWORD_INVALID);
        }
        // 服务端兜底校验，防止绕过前端校验直接调用接口
        if (!newPassword.equals(changePasswordVo.getConfirmPassword())) {
            throw new BookingException(ResultCodeEnum.APP_REGISTER_PASSWORD_INVALID.getCode(),
                    "两次输入的新密码不一致");
        }

        UserInfo userInfo = userInfoMapper.selectByIdWithPassword(userId);
        if (userInfo == null) {
            throw new BookingException(ResultCodeEnum.APP_PASSWORD_NOT_LOGIN);
        }
        if (!PasswordUtil.matches(changePasswordVo.getOldPassword(), userInfo.getPassword())) {
            throw new BookingException(ResultCodeEnum.APP_OLD_PASSWORD_ERROR);
        }
        if (PasswordUtil.matches(newPassword, userInfo.getPassword())) {
            throw new BookingException(ResultCodeEnum.APP_CHANGE_PASSWORD_SAME);
        }

        UserInfo update = new UserInfo();
        update.setId(userId);
        update.setPassword(PasswordUtil.encode(newPassword));
        // 自助改密同时清除管理员重置密码后的强制改密标记
        update.setMustChangePwd(0);
        userInfoMapper.updateById(update);
        // 记录改密时间，使改密前签发的令牌（含可能已泄露的令牌）立即失效
        redisTemplate.opsForValue().set(
                RedisConstant.APP_PASSWORD_CHANGED_PREFIX + userId,
                String.valueOf(System.currentTimeMillis() / 1000),
                RedisConstant.APP_PASSWORD_CHANGED_TTL_SEC, TimeUnit.SECONDS);
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
        UserInfoVo userInfoVo = new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl(),
                userInfo.getMustChangePwd() == null ? 0 : userInfo.getMustChangePwd());
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
