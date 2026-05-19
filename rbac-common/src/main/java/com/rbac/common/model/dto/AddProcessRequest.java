package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加流程请求DTO
 */

@Data
public class AddProcessRequest {

    @NotBlank(message = "流程编码不能为空")
    private String processCode;

    @NotBlank(message = "流程名称不能为空")
    private String processName;

    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
