package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.core.service.RPADataParsingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * RPA数据解析控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system/data/parsing")
@Tag(name = "RPA数据解析", description = "RPA数据解析相关接口")
public class RPADataParsingController {

    private final RPADataParsingService rpaDataParsingService;

    /**
     * 分页查询RPA数据解析列表
     * @param taskId 任务ID
     * @param status 状态
     * @param parsingTimeStart 解析开始时间
     * @param parsingTimeEnd 解析结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("list")
    @Operation(summary = "分页查询RPA数据解析列表", description = "分页查询RPA数据解析列表")
    public Result<?> pageRPADataParsingList(
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String parsingTimeStart,
            @RequestParam(required = false) String parsingTimeEnd,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return rpaDataParsingService.pageRPADataParsingList(taskId, status, parsingTimeStart, parsingTimeEnd, pageNum, pageSize);
    }

    /**
     * 查询RPA数据解析详情
     * @param parsingId 解析ID
     * @return 数据解析详情
     */
    @GetMapping("detail/{parsingId}")
    @Operation(summary = "查询RPA数据解析详情", description = "查询RPA数据解析详情")
    public Result<?> getRPADataParsingDetail(@PathVariable Integer parsingId) {
        return rpaDataParsingService.getRPADataParsingDetail(parsingId);
    }

    /**
     * 删除RPA数据解析
     * @param parsingId 解析ID
     * @return 删除结果
     */
    @DeleteMapping("delete/{parsingId}")
    @Operation(summary = "删除RPA数据解析", description = "删除RPA数据解析")
    public Result<?> deleteRPADataParsing(@PathVariable Integer parsingId) {
        return rpaDataParsingService.deleteRPADataParsing(parsingId);
    }
}
