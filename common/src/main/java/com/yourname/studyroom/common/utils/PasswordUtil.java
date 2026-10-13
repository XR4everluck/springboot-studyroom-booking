package com.yourname.studyroom.common.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码哈希工具：PBKDF2WithHmacSHA256（JDK 内置，无需额外依赖）
 * 存储格式：pbkdf2$&lt;iterations&gt;$&lt;saltBase64&gt;$&lt;hashBase64&gt;
 */
public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final String PREFIX = "pbkdf2$";

    private PasswordUtil() {
    }

    /**
     * 生成加盐哈希
     *
     * @param rawPassword 明文密码
     * @return 可直接入库的哈希串
     */
    public static String encode(String rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword, salt, ITERATIONS);
        return PREFIX + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 校验明文密码与存储哈希是否匹配（常量时间比较）
     */
    public static boolean matches(String rawPassword, String encoded) {
        if (rawPassword == null || encoded == null || !encoded.startsWith(PREFIX)) {
            return false;
        }
        try {
            String[] parts = encoded.substring(PREFIX.length()).split("\\$");
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
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
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
