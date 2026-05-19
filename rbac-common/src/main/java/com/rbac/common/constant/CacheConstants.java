package com.rbac.common.constant;

/**
 * 缓存常量
 */
public interface CacheConstants {

    /**
     * 缓存有效期，默认720（分钟）
     */
    long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    long REFRESH_TIME = 120;

    /**
     * 权限缓存前缀
     */
    String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 用户信息缓存
     */
    String USER_DETAILS = "user_details:";

    /**
     * 字典缓存
     */
    String SYS_DICT_KEY = "sys_dict:";

    /**
     * 参数缓存
     */
    String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 幂等性 redis key
     */
    String IDEMPOTENT_KEY = "idempotent:";
}
