package com.rbac.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密工具类
 */
public class AesUtils {

    /**
     * 算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 转换模式
     */
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 默认密钥（生产环境请使用配置文件）
     */
    private static final String DEFAULT_KEY = "financial-scaffold-aes-key-2024";

    /**
     * 生成密钥
     *
     * @return 密钥
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 加密
     *
     * @param content 明文
     * @return 密文（Base64编码）
     */
    public static String encrypt(String content) {
        return encrypt(content, DEFAULT_KEY);
    }

    /**
     * 加密
     *
     * @param content 明文
     * @param key     密钥
     * @return 密文（Base64编码）
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 解密
     *
     * @param content 密文（Base64编码）
     * @return 明文
     */
    public static String decrypt(String content) {
        return decrypt(content, DEFAULT_KEY);
    }

    /**
     * 解密
     *
     * @param content 密文（Base64编码）
     * @param key     密钥
     * @return 明文
     */
    public static String decrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }
}
