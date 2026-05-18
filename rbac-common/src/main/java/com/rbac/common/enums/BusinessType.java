package com.rbac.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum BusinessType {

    /**
     * 其它
     */
    OTHER(0, "其它"),

    /**
     * 新增
     */
    INSERT(1, "新增"),

    /**
     * 修改
     */
    UPDATE(2, "修改"),

    /**
     * 删除
     */
    DELETE(3, "删除"),

    /**
     * 授权
     */
    GRANT(4, "授权"),

    /**
     * 导出
     */
    EXPORT(5, "导出"),

    /**
     * 导入
     */
    IMPORT(6, "导入"),

    /**
     * 强退
     */
    FORCE(7, "强退"),

    /**
     * 清空数据
     */
    CLEAN(8, "清空数据");

    private final Integer code;
    private final String description;
}
