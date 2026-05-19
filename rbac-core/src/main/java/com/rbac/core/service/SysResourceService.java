package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddResourceRequest;
import com.rbac.common.model.dto.UpdateResourceRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysResource;

import java.util.List;

/**
 * 系统资源服务接口
 */

public interface SysResourceService extends IService<SysResource> {

    /**
     * 根据角色ID列表查询资源
     * @param roleIds 角色ID列表
     * @return 资源列表
     */
    List<SysResource> getResourcesByRoleIds(List<Long> roleIds);

    /**
     * 根据用户ID查询用户权限
     * @param userId 用户ID
     * @return 用户权限列表
     */
    List<String> getPermissionsByUserId(Long userId);

    /**
     * 获取资源树结构
     * @return 资源树结构
     */
    List<SysResource> getResourceTree();

    /**
     * 分页查询资源列表
     * @param tree 是否返回树结构
     * @param resourceName 资源名称（模糊查询）
     * @param resourceType 资源类型（1=菜单，2=按钮/权限点，3=接口/API）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资源列表
     */
    Result<?> pageResourceList(Boolean tree, String resourceName, String resourceType, Integer pageNum, Integer pageSize);

    /**
     * 添加资源
     * @param request 添加资源请求参数
     * @return 添加结果
     */
    Result<?> addResource(AddResourceRequest request);

    /**
     * 更新资源
     * @param resourceId 资源ID
     * @param request 更新资源请求参数
     * @return 更新结果
     */
    Result<?> updateResource(Long resourceId, UpdateResourceRequest request);

    /**
     * 删除资源
     * @param resourceId 资源ID
     * @return 删除结果
     */
    Result<?> deleteResource(Long resourceId);
}
