package com.yourname.studyroom.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 密码哈希工具：新密码统一 BCrypt 加盐哈希（spring-security-crypto，格式 $2a$10$...）；
 * 存量 PBKDF2WithHmacSHA256 哈希（格式 pbkdf2$&lt;iterations&gt;$&lt;saltBase64&gt;$&lt;hashBase64&gt;）仍可校验，
 * 由登录方在校验通过后按 needsRehash + encode 渐进升级为 BCrypt，无需刷库、存量用户无感知。
 */
public class PasswordUtil {

    /** BCrypt 编码器（强度 10），实例线程安全可全局复用 */
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    // 存量 PBKDF2 旧算法参数，仅用于校验旧格式哈希
    private static final String PBKDF2_PREFIX = "pbkdf2$";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_KEY_BITS = 256;

    private PasswordUtil() {
    }

    /**
     * 生成加盐哈希（BCrypt）
     *
     * @param rawPassword 明文密码
     * @return 可直接入库的哈希串
     */
    public static String encode(String rawPassword) {
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文密码与存储哈希是否匹配：按前缀自动分发 BCrypt / 存量 PBKDF2
     */
    public static boolean matches(String rawPassword, String encoded) {
        if (rawPassword == null || encoded == null) {
            return false;
        }
        if (isBcryptFormat(encoded)) {
            try {
                return BCRYPT_ENCODER.matches(rawPassword, encoded);
            } catch (Exception e) {
                return false;
            }
        }
        if (encoded.startsWith(PBKDF2_PREFIX)) {
            return pbkdf2Matches(rawPassword, encoded);
        }
        return false;
    }

    /**
     * 是否为本工具可校验的安全哈希格式（BCrypt 或存量 PBKDF2），
     * 供 MD5 兼容逻辑区分"已是安全哈希"与"待升级的旧 MD5"
     */
    public static boolean isHashFormat(String encoded) {
        return encoded != null && (isBcryptFormat(encoded) || encoded.startsWith(PBKDF2_PREFIX));
    }

    /**
     * 是否为需要升级的存量 PBKDF2 哈希，登录校验通过后调用并回写 BCrypt 哈希
     */
    public static boolean needsRehash(String encoded) {
        return encoded != null && encoded.startsWith(PBKDF2_PREFIX);
    }

    private static boolean isBcryptFormat(String encoded) {
        return encoded.startsWith("$2a$") || encoded.startsWith("$2b$") || encoded.startsWith("$2y$");
    }

    /**
     * 存量 PBKDF2 校验（常量时间比较），仅用于兼容旧数据
     */
    private static boolean pbkdf2Matches(String rawPassword, String encoded) {
        try {
            String[] parts = encoded.substring(PBKDF2_PREFIX.length()).split("\\$");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = pbkdf2(rawPassword, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String rawPassword, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, PBKDF2_KEY_BITS);
            return SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 hash failed", e);
        }
    }

    public static void main(String[] args) {
        // 辅助命令：为初始化数据生成密码哈希
        // 用法：java -cp common/target/classes com.yourname.studyroom.common.utils.PasswordUtil <明文密码>
        if (args.length > 0) {
            System.out.println(encode(args[0]));
        }
    }
}
