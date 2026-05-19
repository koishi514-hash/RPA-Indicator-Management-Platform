package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 执行步骤表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("execution_step")
public class ExecutionStep extends BaseEntity {

    /**
     * 创建时间（数据库中不存在该字段）
     */
    @TableField(exist = false)
    private LocalDateTime createTime;

    /**
     * 更新时间（数据库中不存在该字段）
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;

    /**
     * 关联的执行ID
     */
    private String executionId;

    /**
     * 步骤名称（与流程步骤对应）
     */
    private String stepName;

    /**
     * 步骤类型（如java）
     */
    private String stepType;

    /**
     * 步骤输出结果（JSON格式，便于后续解析）
     */
    private String output;

    /**
     * 步骤执行时间
     */
    private LocalDateTime executeTime;
}