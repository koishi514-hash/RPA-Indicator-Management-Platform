package com.rbac.api.controller;

import com.rbac.common.model.dto.AddResourceRequest;
import com.rbac.common.model.dto.UpdateResourceRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.SysResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资源管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system")
@Tag(name = "资源管理", description = "资源管理接口")
public class ResourceController {

    private final SysResourceService sysResourceService;

    /**
     * 分页查询资源列表
     * @param tree 是否返回树结构
     * @param resourceName 资源名称(模糊查询)
     * @param resourceType 资源类型: MENU-菜单, BUTTON-按钮
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资源列表
     */
    @GetMapping("resources")
    @Operation(summary = "分页查询资源列表", description = "分页查询资源列表, 支持按名称和类型查询, 可以返回树形结构")
    public Result<?> getResourceList(
            @Parameter(description = "是否返回树形结构, true表示返回包含children的树形结构")
            @RequestParam(required = false) Boolean tree,
            @Parameter(description = "资源名称(模糊查询)")
            @RequestParam(required = false) String resourceName,
            @Parameter(description = "资源类型: MENU-菜单, BUTTON-按钮")
            @RequestParam(required = false) String resourceType,
            @Parameter(description = "页码, 默认为1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数, 默认为10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(sysResourceService.pageResourceList(tree, resourceName, resourceType, pageNum, pageSize));
    }

    /**
     * 添加资源
     * @param request 资源添加请求
     * @return 资源ID
     */
    @PostMapping("resources")
    @Operation(summary = "添加资源", description = "添加新的资源")
    public Result<?> addResource(
            @Parameter(description = "资源添加请求")
            @Validated @RequestBody AddResourceRequest request) {
        return sysResourceService.addResource(request);
    }

    /**
     * 更新资源
     * @param resourceId 资源ID
     * @param request 资源更新请求
     * @return 资源ID
     */
    @PutMapping("resources/{resourceId}")
    @Operation(summary = "更新资源", description = "更新指定资源的信息, 支持部分字段更新")
    public Result<?> updateResource(
            @Parameter(description = "资源ID") @PathVariable Long resourceId,
            @Parameter(description = "资源更新请求") @Validated @RequestBody UpdateResourceRequest request){
        return sysResourceService.updateResource(resourceId, request);
    }

    /**
     * 删除资源
     * @param resourceId 资源ID
     * @return 删除结果
     */
    @DeleteMapping("resources/{resourceId}")
    @Operation(summary = "删除资源", description = "删除指定资源")
    public Result<?> deleteResource(@Parameter(description = "资源ID") @PathVariable Long resourceId){
        return sysResourceService.deleteResource(resourceId);
    }
}
