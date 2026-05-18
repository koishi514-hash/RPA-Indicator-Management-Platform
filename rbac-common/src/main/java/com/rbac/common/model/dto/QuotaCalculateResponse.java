package com.rbac.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 额度计算响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCalculateResponse {

    /**
     * 审核规则ID
     */
    private Long quotaRuleId;

    /**
     * 审核规则名称
     */
    private String quotaRuleName;

    /**
     * 计算结果
     */
    private Object calculatedResult;

    /**
     * 状态
     */
    private String status;

    /**
     * 输出数据
     */
    private Map<String, Object> output;

    /**
     * 计算时间
     */
    private LocalDateTime calculatedAt;

    /**
     * Coze 原始响应
     */
    private Map<String, Object> cozeResponse;
}
