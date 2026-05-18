package com.rbac.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加任务请求DTO
 */

@Data
public class AddTaskRequest {

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称长度不能超过64个字符")
    private String taskName;

    @NotNull(message = "流程ID不能为空")
    private Long processId;

    @NotNull(message = "机器人ID不能为空")
    private Long robotId;

    @NotBlank(message = "纳税人识别号不能为空")
    private String taxNo;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @TableField(exist = false)
    private String remark;
}
