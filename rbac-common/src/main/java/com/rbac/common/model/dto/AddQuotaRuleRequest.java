package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuotaRuleRequest {

    @NotBlank(message = "额度名称不能为空")
    @Size(max = 100, message = "额度名称长度不能超过100个字符")
    private String quotaName;

    @NotBlank(message = "指标编码不能为空")
    private String indicatorCodes;

    @NotBlank(message = "判断条件不能为空")
    @Size(max = 1000, message = "判断条件长度不能超过1000个字符")
    private String conditions;

    @NotBlank(message = "计算公式不能为空")
    @Size(max = 1000, message = "计算公式长度不能超过1000个字符")
    private String quotaCalculation;

    @NotBlank(message = "结果变量名称不能为空")
    @Size(max = 50, message = "结果变量名称长度不能超过50个字符")
    private String resultVarName;

    @NotBlank(message = "输出模板不能为空")
    @Size(max = 1000, message = "输出模板长度不能超过1000个字符")
    private String outputTemplate;
}
