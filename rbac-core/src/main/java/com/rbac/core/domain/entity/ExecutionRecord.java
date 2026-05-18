package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 执行记录表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("execution_record")
public class ExecutionRecord extends BaseEntity {

    /**
     * 更新时间（数据库中不存在该字段）
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;

    /**
     * 执行ID（唯一标识）
     */
    private String executionId;

    /**
     * 关联的任务编码
     */
    private String taskCode;

    /**
     * 关联的流程编码
     */
    private String processCode;

    /**
     * 执行任务的机器人编码
     */
    private String robotCode;

    /**
     * 执行状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行时长（如"4秒"、"1分9秒"，便于可视化展示）
     */
    private String duration;
}