package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("process")
public class Process extends BaseEntity {

    /**
     * 流程编码（唯一标识）
     */
    private String processCode;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程描述（说明流程用途）
     */
    private String description;

    /**
     * 流程包含的步骤总数
     */
    private Integer stepCount;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}