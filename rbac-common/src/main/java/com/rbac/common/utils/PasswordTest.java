package com.rbac.common.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        // 数据库中的密码哈希值
        String hashedPassword = "$2a$12$jG5RZ/l6Ce9z0nOYka.8SuAhUPsu8xGTP1St.ZWJP4VBXJahu57SK";
        // 输入的密码
        String inputPassword = "123456";

        // 验证密码
        boolean isMatch = PasswordUtils.matches(inputPassword, hashedPassword);
        System.out.println("密码是否匹配: " + isMatch);

        // 生成123456的哈希值，看看与数据库中的是否一致
        String generatedHash = PasswordUtils.encode(inputPassword);
        System.out.println("生成的哈希值: " + generatedHash);

        // 直接使用BCrypt验证
        BCrypt.Result result = BCrypt.verifyer().verify(inputPassword.toCharArray(), hashedPassword);
        System.out.println("直接使用BCrypt验证: " + result.verified);
    }
}