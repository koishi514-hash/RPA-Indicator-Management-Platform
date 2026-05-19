package com.rbac.common.constant;

/**
 * 通用常量
 */
public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 500;

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * 验证码前缀
     */
    String CAPTCHA_PREFIX = "captcha:";

    /**
     * 登录用户 redis key
     */
    String LOGIN_USER_KEY = "login_tokens:";

    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌
     */
    String TOKEN = "token";

    /**
     * 用户ID字段
     */
    String USER_ID = "userId";

    /**
     * 用户名字段
     */
    String USERNAME = "username";

    /**
     * 部门ID字段
     */
    String DEPT_ID = "deptId";

    /**
     * 请求ID
     */
    String REQUEST_ID = "requestId";

    /**
     * 时间戳
     */
    String TIMESTAMP = "timestamp";

    /**
     * 签名
     */
    String SIGNATURE = "signature";

    /**
     * 随机数
     */
    String NONCE = "nonce";
}
