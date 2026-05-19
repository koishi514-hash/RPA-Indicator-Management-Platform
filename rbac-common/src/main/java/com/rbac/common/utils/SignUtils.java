package com.rbac.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 */
public class SignUtils {

    /**
     * 默认密钥（生产环境请使用配置文件）
     */
    private static final String DEFAULT_SECRET = "financial-scaffold-sign-secret-2024";

    /**
     * 生成签名
     *
     * @param params 参数
     * @return 签名
     */
    public static String sign(Map<String, String> params) {
        return sign(params, DEFAULT_SECRET);
    }

    /**
     * 生成签名
     *
     * @param params 参数
     * @param secret 密钥
     * @return 签名
     */
    public static String sign(Map<String, String> params, String secret) {
        // 按照参数名排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        
        // 拼接参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        
        // 添加密钥
        sb.append("secret=").append(secret);
        
        // MD5加密
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @param params    参数
     * @param signature 签名
     * @return 是否验证通过
     */
    public static boolean verify(Map<String, String> params, String signature) {
        return verify(params, signature, DEFAULT_SECRET);
    }

    /**
     * 验证签名
     *
     * @param params    参数
     * @param signature 签名
     * @param secret    密钥
     * @return 是否验证通过
     */
    public static boolean verify(Map<String, String> params, String signature, String secret) {
        String calculatedSign = sign(params, secret);
        return calculatedSign.equals(signature);
    }

    /**
     * MD5加密
     *
     * @param content 内容
     * @return 加密后的字符串
     */
    public static String md5(String content) {
        return DigestUtils.md5Hex(content);
    }

    /**
     * SHA256加密
     *
     * @param content 内容
     * @return 加密后的字符串
     */
    public static String sha256(String content) {
        return DigestUtils.sha256Hex(content);
    }
}
