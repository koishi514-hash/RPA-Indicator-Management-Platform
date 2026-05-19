package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新流程请求DTO
 */

@Data
public class UpdateProcessRequest {

    @NotNull(message = "流程ID不能为空")
    private Long processId;

    @NotBlank(message = "流程编码不能为空")
    private String processCode;

    @NotBlank(message = "流程名称不能为空")
    private String processName;

    private String description;

    @NotNull(message = "流程状态不能为空")
    private Integer status;
}
