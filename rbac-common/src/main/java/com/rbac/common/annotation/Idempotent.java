package com.rbac.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等性key的前缀
     */
    String prefix() default "idempotent:";

    /**
     * 过期时间（默认1小时）
     */
    long expireTime() default 1;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * 提示消息
     */
    String message() default "请勿重复操作";

    /**
     * 是否删除key（执行完成后删除）
     */
    boolean delKey() default false;
}
