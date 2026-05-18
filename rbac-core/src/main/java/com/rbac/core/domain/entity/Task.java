package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 任务表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task")
public class Task extends BaseEntity {

    /**
     * 更新时间（数据库中不存在该字段）
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;

    /**
     * 任务编码（唯一标识）
     */
    private String taskCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 纳税人识别号（业务核心字段）
     */
    private String taxNo;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 绑定的流程编码
     */
    private String processCode;

    /**
     * 绑定的机器人编码
     */
    private String robotCode;

    /**
     * 任务状态：0-待执行，1-执行中，2-已完成，3-失败
     */
    private Integer status;

    /**
     * 开始执行时间
     */
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 优先级
     */
    private Integer priority;
}