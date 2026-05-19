package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddQuotaRuleRequest;
import com.rbac.common.model.dto.QuotaCalculateRequest;
import com.rbac.common.model.dto.QuotaCalculateResponse;
import com.rbac.common.model.dto.UpdateQuotaRuleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.QuotaRule;

/**
 * 审核规则服务接口
 */
public interface QuotaRuleService extends IService<QuotaRule> {

    /**
     * 分页查询审核规则列表
     *
     * @param keyword 搜索关键词（额度名称）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 审核规则列表
     */
    Result<?> pageQuotaRuleList(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 新增审核规则
     *
     * @param request 新增审核规则请求
     * @return 审核规则ID
     */
    Result<?> addQuotaRule(AddQuotaRuleRequest request);

    /**
     * 更新审核规则
     *
     * @param request 更新审核规则请求
     * @return 更新结果
     */
    Result<?> updateQuotaRule(UpdateQuotaRuleRequest request);

    /**
     * 查询审核规则详情
     *
     * @param id 审核规则ID
     * @return 审核规则详情
     */
    Result<?> getQuotaRuleDetail(Long id);

    /**
     * 删除审核规则
     *
     * @param id 审核规则ID
     * @return 删除结果
     */
    Result<?> deleteQuotaRule(Long id);

    /**
     * 执行额度计算
     *
     * @param request 额度计算请求
     * @return 计算结果
     */
    Result<QuotaCalculateResponse> calculateQuota(QuotaCalculateRequest request);
}
