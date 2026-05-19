package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.core.service.RPADataProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * RPA数据加工控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system/data/processing")
@Tag(name = "RPA数据加工", description = "RPA数据加工相关接口")
public class RPADataProcessingController {

    private final RPADataProcessingService rpaDataProcessingService;

    /**
     * 分页查询RPA数据加工列表
     * @param taskId 任务ID
     * @param status 状态
     * @param processingTimeStart 开始时间
     * @param processingTimeEnd 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    @GetMapping("list")
    @Operation(summary = "分页查询RPA数据加工列表", description = "分页查询RPA数据加工列表")
    public Result<?> pageRPADataProcessingList(
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false)String status,
            @RequestParam(required = false)String processingTimeStart,
            @RequestParam(required = false)String processingTimeEnd,
            @RequestParam(defaultValue = "1")Integer pageNum,
            @RequestParam(defaultValue = "10")Integer pageSize) {
        return rpaDataProcessingService.pageRPADataProcessingList(
                taskId, status, processingTimeStart, processingTimeEnd, pageNum, pageSize);
    }

    /**
     * 获取RPA数据加工详情
     * @param processingId 数据加工ID
     * @return 数据加工详情
     */
    @GetMapping("detail/{processingId}")
    @Operation(summary = "获取RPA数据加工详情", description = "获取RPA数据加工详情")
    public Result<?> getRPADataProcessingDetail(@PathVariable Integer processingId) {
        return rpaDataProcessingService.getRPADataProcessingDetail(processingId);
    }

    /**
     * 删除RPA数据加工
     * @param processingId 数据加工ID
     * @return 删除结果
     */
    @DeleteMapping("delete/{processingId}")
    @Operation(summary = "删除RPA数据加工", description = "删除RPA数据加工")
    public Result<?> deleteRPADataProcessing(@PathVariable Integer processingId) {
        return rpaDataProcessingService.deleteRPADataProcessing(processingId);
    }
}
