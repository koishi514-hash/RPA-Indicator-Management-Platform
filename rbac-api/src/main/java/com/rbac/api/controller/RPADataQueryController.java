package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.core.service.RPADataQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RPA数据查询控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system/data/query")
@Tag(name = "RPA数据查询", description = "RPA数据查询相关接口")
public class RPADataQueryController {

    private final RPADataQueryService rpaDataQueryService;

    /**
     * 分页查询RPA数据查询列表
     */
    @GetMapping("list")
    @Operation(summary = "分页查询RPA数据查询列表", description = "根据关键词、任务ID、税率、状态、创建时间范围分页查询RPA数据查询列表")
    public Result<?> pageRPADataQueryList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)Integer taskId,
            @RequestParam(required = false)String taxAreaId,
            @RequestParam(required = false)Integer status,
            @RequestParam(required = false)String createTimeStart,
            @RequestParam(required = false)String createTimeEnd,
            @RequestParam(defaultValue = "1")Integer pageNum,
            @RequestParam(defaultValue = "10")Integer pageSize) {
        return rpaDataQueryService.pageRPADataQueryList(keyword, taskId, taxAreaId, status, createTimeStart, createTimeEnd, pageNum, pageSize);
    }

    /**
     * 处理任务ID类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        if ("taskId".equals(e.getName())) {
            // 构建空列表响应
            Map<String, Object> data = new HashMap<>();
            data.put("total", 0L);
            data.put("records", new ArrayList<>());
            data.put("pageNum", 1);
            data.put("pageSize", 10);
            return Result.success("请输入合法的任务ID(整数数字)", data);
        }
        return Result.failed(400, "参数错误");
    }

    /**
     * 获取RPA数据查询详情
     */
    @GetMapping("detail/{queryId}")
    @Operation(summary = "获取RPA数据查询详情", description = "根据数据查询ID获取RPA数据查询详情")
    public Result<?> getRPADataQueryDetail(@PathVariable Integer queryId) {
        return rpaDataQueryService.getRPADataQueryDetail(queryId);
    }

    /**
     * 删除RPA数据记录
     */
    @DeleteMapping("delete/{queryId}")
    @Operation(summary = "删除RPA数据查询", description = "根据数据查询ID删除RPA数据查询")
    public Result<?> deleteRPADataQuery(@PathVariable Integer queryId) {
        return rpaDataQueryService.deleteRPADataQuery(queryId);
    }
}
