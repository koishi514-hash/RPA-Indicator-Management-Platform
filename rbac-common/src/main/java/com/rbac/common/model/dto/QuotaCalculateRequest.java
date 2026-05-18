package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 额度计算请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCalculateRequest {

    /**
     * 审核规则ID
     */
    @NotNull(message = "审核规则ID不能为空")
    private Long quotaRuleId;

    /**
     * 输入数据（业务数据，如企业信息、营收、税负等）
     */
    private Map<String, Object> data;
}
