package com.rbac.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key前缀
     */
    String key() default "rate_limit:";

    /**
     * 时间窗口（默认1秒）
     */
    int time() default 1;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 允许的请求次数
     */
    int count() default 10;

    /**
     * 提示消息
     */
    String message() default "访问过于频繁，请稍后再试";
}
