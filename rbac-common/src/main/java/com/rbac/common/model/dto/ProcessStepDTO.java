package com.rbac.common.model.dto;

import lombok.Data;

/**
 * 流程步骤DTO
 */

@Data
public class ProcessStepDTO {

    /**
     * 步骤顺序
     */
    private Integer stepOrder;

    /**
     * 步骤名称
     */
    private String stepName;

    /**
     * 步骤类型
     */
    private String stepType;

    /**
     * 步骤代码内容
     */
    private String codeContent;

    /**
     * 步骤描述
     */
    private String description;
}
