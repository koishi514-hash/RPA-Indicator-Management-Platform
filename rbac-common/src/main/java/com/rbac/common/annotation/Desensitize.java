package com.rbac.common.annotation;

import java.lang.annotation.*;

/**
 * 数据脱敏注解
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {

    /**
     * 脱敏类型
     */
    DesensitizeType type() default DesensitizeType.CUSTOM;

    /**
     * 开始位置（包含）
     */
    int start() default 0;

    /**
     * 结束位置（包含）
     */
    int end() default 0;

    /**
     * 脱敏类型枚举
     */
    enum DesensitizeType {
        /**
         * 自定义
         */
        CUSTOM,

        /**
         * 姓名（保留姓，其他用*代替）
         */
        NAME,

        /**
         * 手机号（保留前3位和后4位）
         */
        MOBILE,

        /**
         * 身份证号（保留前6位和后4位）
         */
        ID_CARD,

        /**
         * 银行卡号（保留前6位和后4位）
         */
        BANK_CARD,

        /**
         * 邮箱（保留@前1位和@后全部）
         */
        EMAIL,

        /**
         * 地址（保留省市，其他用*代替）
         */
        ADDRESS
    }
}
