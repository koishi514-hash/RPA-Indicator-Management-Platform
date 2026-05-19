package com.rbac.common.model.vo;

import lombok.Data;

@Data
public class LoginVO {

    /**
     * 登录凭证（token）
     */
    private String token;

    /**
     * 登录凭证名称（如satoken）
     */
    private String tokenName = "satoken";

    /**
     * 是否登录成功
     */
    private boolean isLogin = true;

    /**
     * 登录ID（如用户ID）
     */
    private String loginId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 登录凭证过期时间（秒）
     */
    private long timeout = 2592000;    // 30天
}
