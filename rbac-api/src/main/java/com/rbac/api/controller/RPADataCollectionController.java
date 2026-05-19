package com.rbac.api.controller;


import com.rbac.common.model.dto.AddRPADataCollectionRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.RPADataCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * RPA数据采集控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system/data/collection")
@Tag(name = "RPA数据采集", description = "RPA数据采集相关接口")
public class RPADataCollectionController {

    private final RPADataCollectionService rpaDataCollectionService;

    /**
     * 分页查询RPA数据采集列表
     * @param taskCode 任务编码
     * @param keyword 关键词
     * @param status 状态
     * @param collectionTimeStart 采集时间开始
     * @param collectionTimeEnd 采集时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("list")
    @Operation(summary = "分页查询RPA数据采集列表", description = "分页查询RPA数据采集列表")
    public Result<?> pageRPADataCollectionList(
            @RequestParam(required = false) String taskCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String collectionTimeStart,
            @RequestParam(required = false) String collectionTimeEnd,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return rpaDataCollectionService.pageRPADataCollectionList(
                taskCode, keyword, status, collectionTimeStart, collectionTimeEnd, pageNum, pageSize);
    }

    /**
     * 获取RPA数据采集详情
     * @param collectionId 数据采集ID
     * @return 数据采集详情
     */
    @GetMapping("detail/{collectionId}")
    @Operation(summary = "获取RPA数据采集详情", description = "获取RPA数据采集详情")
    public Result<?> getRPADataCollectionDetail(@PathVariable Long collectionId) {
        return rpaDataCollectionService.getRPADataCollectionDetail(collectionId);
    }

    /**
     * 删除RPA数据采集
     * @param collectionId 数据采集ID
     * @return 删除结果
     */
    @DeleteMapping("delete/{collectionId}")
    @Operation(summary = "删除RPA数据采集", description = "删除RPA数据采集")
    public Result<?> deleteRPADataCollection(@PathVariable Long collectionId) {
        return rpaDataCollectionService.deleteRPADataCollection(collectionId);
    }

    /**
     * 添加RPA数据采集
     * @param request 添加请求
     * @return 添加结果
     */
    @PostMapping("add")
    @Operation(summary = "添加RPA数据采集", description = "添加RPA数据采集")
    public Result<?> addRPADataCollection(@RequestBody AddRPADataCollectionRequest request) {
        return rpaDataCollectionService.addRPADataCollection(request);
    }
}
