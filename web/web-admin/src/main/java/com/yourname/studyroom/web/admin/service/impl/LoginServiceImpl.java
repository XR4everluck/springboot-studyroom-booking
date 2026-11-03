package com.yourname.studyroom.web.admin.service.impl;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.utils.JwtUtil;
import com.yourname.studyroom.common.utils.PasswordUtil;
import com.yourname.studyroom.model.entity.SystemUser;
import com.yourname.studyroom.model.enums.BaseStatus;
import com.yourname.studyroom.web.admin.mapper.SystemUserMapper;
import com.yourname.studyroom.web.admin.service.LoginService;
import com.yourname.studyroom.web.admin.vo.login.LoginVo;
import com.yourname.studyroom.web.admin.vo.system.user.SystemUserInfoVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    SystemUserMapper systemUserMapper;

    @Override
    public String login(LoginVo loginVo) {
        SystemUser systemUser = systemUserMapper.selectOneByUsername(loginVo.getUsername());

        if (systemUser == null) {
            // 管理端无此账号：若凭证恰好匹配用户端账号，引导其到用户端登录
            hintIfValidAppUser(loginVo.getUsername(), loginVo.getPassword());
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        if (systemUser.getStatus() == BaseStatus.DISABLE){
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }

        // 只导入建表脚本而未导入初始化数据时，账号密码字段可能为空，此处按密码错误处理而不是抛空指针
        if (systemUser.getPassword() == null
                || !matchesAndUpgrade(systemUser, loginVo.getPassword())){
            hintIfValidAppUser(loginVo.getUsername(), loginVo.getPassword());
            throw new BookingException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }

        return JwtUtil.createToken(systemUser.getId(), systemUser.getUsername(), LoginUserType.ADMIN);
    }

    /**
     * 账号不存在或密码错误时，若该账号实为用户端账号（user_info）且密码正确，
     * 抛出专用提示引导用户前往用户端登录；仅在凭证完全正确时提示，不泄露账号归属
     */
    private void hintIfValidAppUser(String username, String rawPassword) {
        String storedHash = systemUserMapper.selectAppUserPasswordByUsername(username);
        if (storedHash != null && PasswordUtil.matches(rawPassword, storedHash)) {
            throw new BookingException(ResultCodeEnum.ADMIN_LOGIN_IS_APP_USER);
        }
    }

    /**
     * 密码校验：BCrypt/PBKDF2 哈希优先；存量 MD5 哈希校验通过后自动升级为 BCrypt 落库
     */
    private boolean matchesAndUpgrade(SystemUser systemUser, String rawPassword) {
        String stored = systemUser.getPassword();
        if (PasswordUtil.isHashFormat(stored)) {
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
