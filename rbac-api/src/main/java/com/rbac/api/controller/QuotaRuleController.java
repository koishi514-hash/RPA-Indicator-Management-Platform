package com.rbac.api.controller;

import com.rbac.common.model.dto.AddQuotaRuleRequest;
import com.rbac.common.model.dto.QuotaCalculateRequest;
import com.rbac.common.model.dto.QuotaCalculateResponse;
import com.rbac.common.model.dto.UpdateQuotaRuleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.QuotaRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 指标审核控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quota-rules")
@Tag(name = "指标审核", description = "额度计算和审核相关接口")
public class QuotaRuleController {

    private final QuotaRuleService quotaRuleService;

    /**
     * 分页查询审核规则列表
     *
     * @param keyword 搜索关键词（额度名称）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 审核规则列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询审核规则列表", description = "根据搜索关键词分页查询审核规则列表")
    public Result<?> pageQuotaRuleList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return quotaRuleService.pageQuotaRuleList(keyword, pageNum, pageSize);
    }

    /**
     * 新增审核规则
     *
     * @param request 新增审核规则请求
     * @return 新增结果
     */
    @PostMapping("/create")
    @Operation(summary = "新增审核规则", description = "新增审核规则")
    public Result<?> addQuotaRule(@Validated @RequestBody AddQuotaRuleRequest request) {
        return quotaRuleService.addQuotaRule(request);
    }

    /**
     * 更新审核规则
     *
     * @param request 更新审核规则请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新审核规则", description = "更新审核规则")
    public Result<?> updateQuotaRule(@Validated @RequestBody UpdateQuotaRuleRequest request) {
        return quotaRuleService.updateQuotaRule(request);
    }

    /**
     * 查询审核规则详情
     *
     * @param id 审核规则ID
     * @return 审核规则详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "查询审核规则详情", description = "根据审核规则ID查询审核规则详情")
    public Result<?> getQuotaRuleDetail(@PathVariable Long id) {
        return quotaRuleService.getQuotaRuleDetail(id);
    }

    /**
     * 删除审核规则
     *
     * @param id 审核规则ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除审核规则", description = "根据审核规则ID删除审核规则")
    public Result<?> deleteQuotaRule(@PathVariable Long id) {
        return quotaRuleService.deleteQuotaRule(id);
    }

    /**
     * 执行额度计算
     *
     * @param request 额度计算请求
     * @return 计算结果
     */
    @PostMapping("/calculate")
    @Operation(summary = "执行指标审核", description = "根据审核规则和业务数据执行额度计算，返回审核结果")
    public Result<QuotaCalculateResponse> calculateQuota(@Validated @RequestBody QuotaCalculateRequest request) {
        return quotaRuleService.calculateQuota(request);
    }
}
