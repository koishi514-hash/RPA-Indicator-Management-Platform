package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddRoleRequest;
import com.rbac.common.model.dto.UpdateRoleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysRole;

import java.util.List;

/**
 * 系统角色服务接口
 */

public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID查询用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 根据用户ID查询用户角色编码列表
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getRoleCodesByUserId(Long userId);

    /**
     * 根据用户ID查询用户角色名称列表
     * @param userId 用户ID
     * @return 角色名称列表
     */
    List<String> getRoleNamesByUserId(Long userId);

    /**
     * 根据角色代码查询用户ID列表
     * @param roleCode 角色代码
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleCode(String roleCode);

    /**
     * 分页查询角色列表
     * @param roleCode 角色编码查询参数
     * @param roleName 角色名称查询参数
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Result<?> pageRoleList(String roleCode, String roleName, Integer pageNum, Integer pageSize);

    /**
     * 添加角色
     * @param request 添加角色请求
     * @return 添加结果
     */
    Result<?> addRole(AddRoleRequest request);

    /**
     * 更新角色(权限分配)
     * @param request 更新角色请求
     * @return 更新结果
     */
    Result<?> updateRole(Long roleId, UpdateRoleRequest request);

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 删除结果
     */
    Result<?> deleteRole(Long roleId);

}
