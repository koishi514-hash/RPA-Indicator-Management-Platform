package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 机器人表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("robot")
public class Robot extends BaseEntity {

    /**
     * 机器人编码（唯一标识）
     */
    private String robotCode;

    /**
     * 机器人名称
     */
    private String robotName;

    /**
     * 机器人类型（如测试机器人、thread）
     */
    private String robotType;

    /**
     * 状态：0-离线，1-在线，2-故障
     */
    private Integer status;

    /**
     * 当前执行任务编码（空闲为NULL）
     */
    private String currentTaskId;

    /**
     * 最后心跳时间（用于监控在线状态）
     */
    private LocalDateTime lastHeartbeat;

    /**
     * 描述
     */
    private String description;
}