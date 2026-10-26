package com.yourname.studyroom.common.utils;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUserType;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;

/**
 * JwtToken工具类
 * <p>
 * 签名密钥优先取环境变量 JWT_SECRET（也支持 -Djwt.secret 系统属性），
 * 未配置时每次启动随机生成——重启后所有已签发令牌失效，需重新登录。
 * 令牌携带 userType claim（admin/app），由各端拦截器校验，防止跨端冒用。
 */

public class JwtUtil {

    /** 令牌有效期：7 天 */
    private static final long TOKEN_TTL_MILLIS = 3600000L * 24 * 7;

    private static final SecretKey secretKey = initSecretKey();

    private static SecretKey initSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.getBytes().length < 32) {
            secret = System.getProperty("jwt.secret");
        }
        if (secret == null || secret.getBytes().length < 32) {
            // 未配置密钥时退化为进程内随机密钥（HS256 要求至少 32 字节）
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            return Keys.hmacShaKeyFor(bytes);
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成令牌
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @param userType 用户类型（admin/app），用于拦截器区分令牌归属端
     * @return 生成的令牌字符串
     */
    public static String createToken(Long userId, String userName, LoginUserType userType) {
        return Jwts.builder().
                setExpiration(new Date(System.currentTimeMillis() + TOKEN_TTL_MILLIS)).
                setSubject("USER_INFO").
                claim("userId", userId).
                claim("userName", userName).
                claim("userType", userType.getCode()).
                signWith(secretKey, SignatureAlgorithm.HS256).
                compact();
    }

    /**
     * 解析JWT令牌
     * <p>
     * 此方法用于解析传入的JWT令牌，并返回令牌中的Claims对象。
     * 如果令牌无效或过期，将抛出异常。
     *
     * @param token 待解析的JWT令牌
     * @return Claims对象，包含令牌中的信息
     * @throws BookingException 如果令牌为空、过期或无效，则抛出异常
     */
    public static Claims parseToken(String token) {
        if (token == null) {
            throw new BookingException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (ExpiredJwtException e) {
            throw new BookingException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new BookingException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    /**
     * 校验令牌中的用户类型与当前服务端是否匹配，不匹配抛 token 非法
     */
    public static void checkUserType(Claims claims, LoginUserType expected) {
        String userType = claims.get("userType", String.class);
        if (LoginUserType.fromCode(userType) != expected) {
            throw new BookingException(ResultCodeEnum.TOKEN_INVALID);
        }
    }
}
