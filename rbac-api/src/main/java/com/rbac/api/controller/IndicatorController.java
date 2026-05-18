package com.rbac.api.controller;

import com.rbac.common.model.dto.AddIndicatorRequest;
import com.rbac.common.model.dto.UpdateIndicatorRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.IndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 指标管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/indicators")
@Tag(name = "指标管理", description = "指标配置和管理相关接口")
public class IndicatorController {

    private final IndicatorService indicatorService;

    /**
     * 分页查询指标列表
     * @param keyword 搜索关键词（指标名称或编码）
     * @param pageNum 页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @return 指标列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询指标列表", description = "根据搜索关键词分页查询指标列表")
    public Result<?> pageIndicatorList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return indicatorService.pageIndicatorList(keyword, pageNum, pageSize);
    }

    /**
     * 新增指标
     * @param request 新增指标请求
     * @return 新增结果
     */
    @PostMapping("/create")
    @Operation(summary = "新增指标", description = "新增指标")
    public Result<?> addIndicator(@Validated @RequestBody AddIndicatorRequest request) {
        return indicatorService.addIndicator(request);
    }

    /**
     * 更新指标
     * @param request 更新指标请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新指标", description = "更新指标")
    public Result<?> updateIndicator(@Validated @RequestBody UpdateIndicatorRequest request) {
        return indicatorService.updateIndicator(request);
    }

    /**
     * 获取指标详情
     * @param id 指标ID
     * @return 指标详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取指标详情", description = "根据指标ID查询指标详情")
    public Result<?> getIndicatorDetail(@PathVariable Long id) {
        return indicatorService.getIndicatorDetail(id);
    }

    /**
     * 删除指标
     * @param id 指标ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除指标", description = "根据指标ID删除指标")
    public Result<?> deleteIndicator(@PathVariable Long id) {
        return indicatorService.deleteIndicator(id);
    }

    /**
     * 获取所有指标列表（供审核规则选择使用）
     * @return 指标列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有指标列表", description = "获取所有指标列表，供审核规则选择使用")
    public Result<?> getAllIndicators() {
        return indicatorService.getAllIndicators();
    }
}
