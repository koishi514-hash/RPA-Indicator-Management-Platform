package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程步骤表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("process_step")
public class ProcessStep extends BaseEntity {

    /**
     * 关联流程编码
     */
    private String processCode;

    /**
     * 步骤执行顺序（从1开始递增）
     */
    private Integer stepOrder;

    /**
     * 步骤名称（如"采集(java)"）
     */
    private String stepName;

    /**
     * 步骤类型（如Java爬虫代码、Groovy爬虫代码）
     */
    private String stepType;

    /**
     * 步骤执行的代码内容
     */
    private String codeContent;

    /**
     * 描述
     */
    private String description;
}