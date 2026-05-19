package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新任务请求DTO
 */

@Data
public class UpdateTaskRequest {

    @NotNull(message = "任务ID不能为空")
    private Long id;

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @NotNull(message = "流程ID不能为空")
    private Long processId;

    @NotNull(message = "机器人ID不能为空")
    private Long robotId;

    @NotBlank(message = "企业税号不能为空")
    private String taxNo;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    private String remark;
}
