package com.rbac.common.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * 密码加密工具类
 */
public class PasswordUtils {

    /**
     * BCrypt强度（4-31，推荐12）
     */
    private static final int BCRYPT_COST = 12;

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }

    /**
     * 验证密码
     *
     * @param password 明文密码
     * @param hashed   加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String password, String hashed) {
        // 检查哈希值格式，如果缺少$2a$前缀，添加它
        if (hashed != null && hashed.startsWith("$12$")) {
            hashed = "$2a" + hashed;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashed);
        return result.verified;
    }
}