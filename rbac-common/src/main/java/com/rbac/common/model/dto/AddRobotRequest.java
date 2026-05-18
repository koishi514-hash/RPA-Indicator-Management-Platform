package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加机器人请求DTO
 */

@Data
public class AddRobotRequest {

    @NotBlank(message = "机器人编码不能为空")
    private String robotCode;

    @NotBlank(message = "机器人名称不能为空")
    private String robotName;

    @NotBlank(message = "机器人类型不能为空")
    private String robotType;

    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
