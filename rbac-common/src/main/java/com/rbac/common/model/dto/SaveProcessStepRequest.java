package com.rbac.common.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 保存流程步骤请求DTO
 */

@Data
public class SaveProcessStepRequest {

    /**
     * 关联流程编码
     */
    private String processCode;

    /**
     * 流程步骤列表
     */
    private List<ProcessStepDTO> steps;
}
