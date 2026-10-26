package com.yourname.studyroom.web.admin.service.impl;

import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.web.admin.mapper.SystemUserMapper;
import com.yourname.studyroom.web.admin.service.LoginService;
import com.yourname.studyroom.web.admin.vo.login.CaptchaVo;
import com.yourname.studyroom.web.admin.vo.login.LoginVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserInfoVo;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);

        String code = specCaptcha.text().toLowerCase();
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();

        redisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
        return new CaptchaVo(specCaptcha.toBase64(), key);
    }

    @Override
    public String login(LoginVo loginVo) {
        if (loginVo.getCaptchaCode() == null) {
            throw new BookingException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }

        String code = redisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if (code == null) {
            throw new BookingException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }
        if (!code.equals(loginVo.getCaptchaCode().toLowerCase())) {
            throw new BookingException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }

        SystemUser systemUser = systemUserMapper.selectOneByUsername(loginVo.getUsername());

        if (systemUser == null) {
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        if (systemUser.getStatus() == BaseStatus.DISABLE){
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }

        // 只导入建表脚本而未导入初始化数据时，账号密码字段可能为空，此处按密码错误处理而不是抛空指针
        if (systemUser.getPassword() == null
                || !matchesAndUpgrade(systemUser, loginVo.getPassword())){
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }

        return JwtUtil.createToken(systemUser.getId(), systemUser.getUsername(), LoginUserType.ADMIN);
    }

    /**
     * 密码校验：优先 PBKDF2；存量 MD5 哈希校验通过后自动升级为 PBKDF2 落库
     */
    private boolean matchesAndUpgrade(SystemUser systemUser, String rawPassword) {
        String stored = systemUser.getPassword();
        if (stored.startsWith("pbkdf2$")) {
            return PasswordUtil.matches(rawPassword, stored);
        }
        if (stored.equals(DigestUtils.md5Hex(rawPassword))) {
            SystemUser upgrade = new SystemUser();
            upgrade.setId(systemUser.getId());
            upgrade.setPassword(PasswordUtil.encode(rawPassword));
            systemUserMapper.updateById(upgrade);
            return true;
        }
        return false;
    }

    @Override
    public SystemUserInfoVo getLoginUserInfo(Long userId) {
        SystemUser systemUser = systemUserMapper.selectById(userId);
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setName(systemUser.getName());
        systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());
        return systemUserInfoVo;
    }
}
