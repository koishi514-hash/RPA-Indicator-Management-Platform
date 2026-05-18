package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指标表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("indicator")
public class Indicator extends BaseEntity {

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标编码（唯一标识）
     */
    private String indicatorCode;

    /**
     * 指标逻辑描述
     */
    private String indicatorLogic;

    /**
     * 关联的任务ID（数据来源）
     */
    private Long taskId;
}
