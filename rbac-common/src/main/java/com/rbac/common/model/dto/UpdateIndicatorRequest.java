package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新指标请求DTO
 */
@Data
public class UpdateIndicatorRequest {

    /**
     * 指标ID
     */
    @NotNull(message = "指标ID不能为空")
    private Long id;

    /**
     * 指标名称
     */
    @NotBlank(message = "指标名称不能为空")
    @Size(max = 100, message = "指标名称长度不能超过100个字符")
    private String indicatorName;

    /**
     * 指标编码
     */
    @NotBlank(message = "指标编码不能为空")
    @Size(max = 50, message = "指标编码长度不能超过50个字符")
    private String indicatorCode;

    /**
     * 指标逻辑描述
     */
    @NotBlank(message = "指标逻辑不能为空")
    @Size(max = 500, message = "指标逻辑长度不能超过500个字符")
    private String indicatorLogic;

    /**
     * 关联的任务ID
     */
    private Long taskId;
}
