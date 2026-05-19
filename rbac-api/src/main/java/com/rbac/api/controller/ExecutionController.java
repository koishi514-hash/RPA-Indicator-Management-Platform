package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.core.service.ExecutionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 执行记录管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system/executions")
@Tag(name = "执行记录接口", description = "执行记录相关接口")
public class ExecutionController {

    private final ExecutionsService executionsService;

    /**
     * 分页查询执行记录列表
     * @param taskCode 任务编码
     * @param status 执行状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("list")
    @Operation(summary = "分页查询执行记录列表", description = "根据任务ID、执行状态、开始时间、结束时间、页码、每页数量分页查询执行记录列表")
    public Result<?> pageExecutionList(
            @RequestParam(required = false) String taskCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return executionsService.pageExecutionList(taskCode, status, startTime, endTime, pageNum, pageSize);
    }

    /**
     * 获取单个执行记录详情
     * @param executionId 执行记录ID
     * @return 执行记录详情
     */
    @GetMapping("detail/{executionId}")
    @Operation(summary = "获取单个执行记录详情", description = "根据执行记录ID获取单个执行记录详情")
    public Result<?> getExecutionDetail(@PathVariable String executionId) {
        return executionsService.getExecutionDetail(executionId);
    }
}
