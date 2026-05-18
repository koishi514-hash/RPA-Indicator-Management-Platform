package com.rbac.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    /**
     * 正常
     */
    NORMAL(0, "正常"),

    /**
     * 禁用
     */
    DISABLED(1, "禁用"),

    /**
     * 已删除
     */
    DELETED(2, "已删除");

    private final Integer code;
    private final String description;
}
