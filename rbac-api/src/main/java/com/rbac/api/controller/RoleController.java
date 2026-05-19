package com.rbac.api.controller;

import com.rbac.common.model.dto.AddRoleRequest;
import com.rbac.common.model.dto.UpdateRoleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.SysResourceService;
import com.rbac.core.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system")
@Tag(name = "角色管理", description = "角色相关接口")
public class RoleController {

    private final SysRoleService sysRoleService;
    private final SysResourceService sysResourceService;

    /**
     * 分页查询角色列表
     * @param roleCode 角色编码查询参数
     * @param roleName 角色名称查询参数
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("roles")
    @Operation(summary = "分页查询角色列表", description = "根据角色编码、角色名称分页查询角色列表")
    public Result<?> pageRoleList(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return sysRoleService.pageRoleList(roleCode, roleName, pageNum, pageSize);
    }

    /**
     * 添加角色
     * @param request 添加角色请求
     * @return 添加结果
     */
    @PostMapping("roles")
    @Operation(summary = "添加角色", description = "根据添加角色请求参数添加角色到数据库")
    public Result<?> addRole(@Validated @RequestBody AddRoleRequest request) {
        return sysRoleService.addRole(request);
    }

    /**
     * 更新角色(权限分配)
     * @param roleId 角色ID
     * @param request 更新角色请求
     * @return 更新结果
     */
    @PutMapping("roles/{roleId}")
    @Operation(summary = "更新角色", description = "根据更新ID和更新角色请求参数更新角色信息")
    public Result<?> updateRole(@PathVariable Long roleId, @Validated @RequestBody UpdateRoleRequest request) {
        return sysRoleService.updateRole(roleId, request);
    }

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 删除结果
     */
    @DeleteMapping("roles/{roleId}")
    @Operation(summary = "删除角色", description = "根据删除ID删除角色信息")
    public Result<?> deleteRole(@PathVariable Long roleId) {
        return sysRoleService.deleteRole(roleId);
    }

    /**
     * 获取角色关联的资源树(更新角色时权限分配)
     * @return 资源树
     */
    @GetMapping("roles/resources")
    @Operation(summary = "获取资源信息", description = "根据角色ID获取角色关联的资源树")
    public Result<?> getResources() {
        try {
            var resources = sysResourceService.getResourceTree();
            return Result.success("resources", resources);
        } catch (Exception e) {
            return Result.failed(500, "获取资源树失败");
        }
    }
}
