package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddResourceRequest;
import com.rbac.common.model.dto.UpdateResourceRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysResource;
import com.rbac.core.domain.entity.SysRoleResource;
import com.rbac.core.domain.entity.SysUserRole;
import com.rbac.core.domain.mapper.SysResourceMapper;
import com.rbac.core.domain.mapper.SysRoleResourceMapper;
import com.rbac.core.domain.mapper.SysUserRoleMapper;
import com.rbac.core.service.SysResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统资源服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

    private final SysResourceMapper sysResourceMapper;
    private final SysRoleResourceMapper sysRoleResourceMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 根据角色ID查询资源列表
     * @param roleIds 角色ID列表
     * @return 资源列表
     */

    @Override
    public List<SysResource> getResourcesByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()){
            return List.of();
        }

        // 查询角色 - 资源关联
        List<SysRoleResource> roleResources = sysRoleResourceMapper.selectList(new LambdaQueryWrapper<SysRoleResource>()
                .in(SysRoleResource::getRoleId, roleIds));

        if (roleResources == null || roleResources.isEmpty()){
            return List.of();
        }

        // 提取资源ID
        List<Long> resourceIds = roleResources.stream()
                .map(SysRoleResource::getResourceId)
                .collect(Collectors.toList());

        // 查询资源信息
        List<SysResource> resources = baseMapper.selectBatchIds(resourceIds);
        return resources != null ? resources : List.of();
    }

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        // 查询用户-角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (userRoles == null || userRoles.isEmpty()) {
            return List.of();
        }

        // 提取角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询资源列表
        List<SysResource> resources = getResourcesByRoleIds(roleIds);

        if (resources == null || resources.isEmpty()) {
            return List.of();
        }

        // 提取权限路径
        return resources.stream()
                .map(SysResource::getPath)
                .collect(Collectors.toList());
    }

    /**
     * 构建资源树结构
     * @return 资源树结构
     */
    @Override
    public List<SysResource> getResourceTree() {
        // 查询所有资源
        List<SysResource> allResources = baseMapper.selectList(null);

        if (allResources == null || allResources.isEmpty()) {
            return List.of();
        }

        // 构建树结构
        return buildResourceTree(allResources);
    }

    /**
     * 构建资源树
     * @param resources 所有资源列表
     * @return 资源树列表
     */
    private List<SysResource> buildResourceTree(List<SysResource> resources) {
        // 查找根节点
        List<SysResource> rootResources = resources.stream()
                .filter(resource -> resource.getParentId() == null || resource.getParentId() == 0)
                .collect(Collectors.toList());

        // 为每个根节点递归设置子节点
        for (SysResource root : rootResources) {
            root.setChildren(findChildren(root.getId(), resources));
        }
        return rootResources;
    }

    /**
     * 递归查询子资源
     * @param parentId 父资源ID
     * @param resources 所有资源列表
     * @return 子资源列表
     */
    private List<SysResource> findChildren(Long parentId, List<SysResource> resources) {
        List<SysResource> children = resources.stream()
                .filter(resource -> resource.getParentId() != null && resource.getParentId().equals(parentId))
                .collect(Collectors.toList());
        // 递归查找子节点
        for (SysResource child : children) {
            child.setChildren(findChildren(child.getId(), resources));
        }
        return children;
    }

    /**
     * 分页查询资源列表
     * @param tree 是否返回树结构
     * @param resourceName 资源名称（模糊查询）
     * @param resourceType 资源类型（MENU-菜单，BUTTON-按钮）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 资源列表
     */
    @Override
    public Result<?> pageResourceList(Boolean tree, String resourceName, String resourceType, Integer pageNum, Integer pageSize) {
        if (tree != null && tree) {
            // 查询所有资源
            List<SysResource> allResources = null;
            try {
                allResources = baseMapper.selectList(null);
            } catch (Exception e) {
                log.error("查询资源列表失败: {}", e.getMessage());
            }
            
            if (allResources == null || allResources.isEmpty()) {
                Page<SysResource> emptyPage = new Page<>(pageNum, pageSize);
                emptyPage.setTotal(0);
                emptyPage.setRecords(new ArrayList<>());
                return Result.success("success", emptyPage);
            }
            
            // 构建完整的树形结构
            List<SysResource> resourceTree = buildResourceTree(allResources);
            
            // 根据条件筛选树形结构
            List<SysResource> filteredTree = filterResourceTree(resourceTree, resourceName, resourceType);
            
            // 构建分页对象
            Page<SysResource> page = new Page<>(pageNum, pageSize);
            page.setTotal(filteredTree != null ? filteredTree.size() : 0);
            
            if (filteredTree != null && !filteredTree.isEmpty()) {
                // 计算分页数据
                int start = (pageNum - 1) * pageSize;
                int end = Math.min(start + pageSize, filteredTree.size());
                List<SysResource> pageData = filteredTree.subList(start, end);
                page.setRecords(pageData);
            } else {
                page.setRecords(new ArrayList<>());
            }
            
            return Result.success("success", page);
        } else {
            // 分页查询
            Page<SysResource> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<SysResource> queryWrapper = new LambdaQueryWrapper<>();

            if (resourceName != null && !resourceName.isEmpty()) {
                queryWrapper.like(SysResource::getResourceName, resourceName);
            }
            if (resourceType != null && !resourceType.isEmpty()) {
                try{
                    Integer type = Integer.parseInt(resourceType);
                    queryWrapper.eq(SysResource::getResourceType, type);
                } catch (NumberFormatException e) {
                    log.warn("资源类型转换为整数失败，资源类型：{}，异常信息：{}", resourceType, e.getMessage());
                }
            }

            try {
                page = page(page, queryWrapper);
            } catch (Exception e) {
                // 发生异常时，返回空分页对象
                page.setTotal(0);
                page.setRecords(new ArrayList<>());
            }
            
            return Result.success("success", page);
        }
    }
    
    /**
     * 筛选资源树，保留符合条件的节点及其所有父节点
     * @param resourceTree 资源树
     * @param resourceName 资源名称（模糊查询）
     * @param resourceType 资源类型（MENU-菜单，BUTTON-按钮）
     * @return 筛选后的资源树
     */
    private List<SysResource> filterResourceTree(List<SysResource> resourceTree, String resourceName, String resourceType) {
        List<SysResource> filteredTree = new ArrayList<>();
        
        for (SysResource root : resourceTree) {
            SysResource filteredRoot = filterResourceNode(root, resourceName, resourceType);
            if (filteredRoot != null) {
                filteredTree.add(filteredRoot);
            }
        }
        
        return filteredTree;
    }
    
    /**
     * 递归筛选资源节点
     * @param node 资源节点
     * @param resourceName 资源名称（模糊查询）
     * @param resourceType 资源类型（MENU-菜单，BUTTON-按钮）
     * @return 筛选后的资源节点，如果不符合条件则返回null
     */
    private SysResource filterResourceNode(SysResource node, String resourceName, String resourceType) {
        // 检查当前节点是否符合条件
        boolean nodeMatches = matchesCondition(node, resourceName, resourceType);
        
        // 检查子节点是否符合条件
        List<SysResource> filteredChildren = new ArrayList<>();
        if (node.getChildren() != null) {
            for (SysResource child : node.getChildren()) {
                SysResource filteredChild = filterResourceNode(child, resourceName, resourceType);
                if (filteredChild != null) {
                    filteredChildren.add(filteredChild);
                }
            }
        }
        
        // 如果当前节点符合条件，或者有符合条件的子节点，则保留该节点
        if (nodeMatches || !filteredChildren.isEmpty()) {
            SysResource filteredNode = new SysResource();
            BeanUtils.copyProperties(node, filteredNode);
            filteredNode.setChildren(filteredChildren);
            return filteredNode;
        }
        
        // 不符合条件，返回null
        return null;
    }
    
    /**
     * 检查资源是否符合条件
     * @param resource 资源
     * @param resourceName 资源名称（模糊查询）
     * @param resourceType 资源类型（数字类型：1-菜单，2-按钮）
     * @return 是否符合条件
     */
    private boolean matchesCondition(SysResource resource, String resourceName, String resourceType) {
        // 检查资源名称
        if (resourceName != null && !resourceName.isEmpty()){
            if (resource.getResourceName() == null || !resource.getResourceName().contains(resourceName)){
                return false;
            }
        }

        // 检查资源类型
        if (resourceType != null && !resourceType.isEmpty()) {
            try {
                Integer type = Integer.parseInt(resourceType);
                return resource.getResourceType() != null && resource.getResourceType().equals(type);
            } catch (NumberFormatException e) {
                log.warn("资源类型转换为整数失败，资源类型：{}，异常信息：{}", resourceType, e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 添加资源
     * @param request 资源添加请求
     * @return 资源ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addResource(AddResourceRequest request) {
        try {
            // 检查资源编码是否已存在
            LambdaQueryWrapper<SysResource> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysResource::getResourceCode, request.getResourceCode());
            SysResource existingResource = baseMapper.selectOne(queryWrapper);
            if (existingResource != null) {
                return Result.failed(400, "资源编码已存在");
            }

            // 创建资源实体
            SysResource resource = new SysResource();
            resource.setResourceCode(request.getResourceCode());
            resource.setResourceName(request.getResourceName());
            if (request.getResourceType() != 1 && request.getResourceType() != 2 && request.getResourceType() != 3) {
                return Result.failed(400, "资源类型错误");
            } else {
                resource.setResourceType(request.getResourceType());
            }
            resource.setParentId(request.getParentId());
            resource.setPath(request.getPath());
            resource.setIcon(request.getIcon());
            resource.setSortOrder(request.getSortOrder());
            resource.setStatus(request.getStatus());
            resource.setCreateTime(LocalDateTime.now());
            resource.setUpdateTime(LocalDateTime.now());

            // 保存资源
            boolean saved = save(resource);
            if (saved) {
                return Result.success("资源创建成功", resource);
            } else {
                return Result.failed(500, "资源创建失败");
            }
        } catch (Exception e) {
            log.error("资源创建失败", e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "资源创建失败");
        }
    }

    /**
     * 更新资源
     * @param resourceId 资源ID
     * @param request 资源更新请求
     * @return 资源ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateResource(Long resourceId, UpdateResourceRequest request) {
        try {
            SysResource resource = sysResourceMapper.selectById(resourceId);
            if (resource == null) {
                return Result.failed(400, "资源不存在");
            }

            // 更新资源信息
            if (request.getParentId() != null) {
                resource.setParentId(request.getParentId());
            }
            if (request.getResourceName() != null) {
                resource.setResourceName(request.getResourceName());
            }
            if (request.getResourceType() != null) {
                if (request.getResourceType() != 1 && request.getResourceType() != 2 && request.getResourceType() != 3) {
                    return Result.failed(400, "资源类型错误");
                } else {
                    resource.setResourceType(request.getResourceType());
                }
            }
            if (request.getPath() != null) {
                resource.setPath(request.getPath());
            }
            if (request.getIcon() != null) {
                resource.setIcon(request.getIcon());
            }
            if (request.getSortOrder() != null) {
                resource.setSortOrder(request.getSortOrder());
            }
            if (request.getStatus() != null) {
                resource.setStatus(request.getStatus());
            }

            resource.setUpdateTime(LocalDateTime.now());

            // 更新资源
            boolean updated = updateById(resource);
            if (updated) {
                return Result.success("资源更新成功", resource);
            } else {
                return Result.failed(500, "资源更新失败");
            }
        } catch (Exception e) {
            log.error("资源更新失败", e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "资源更新失败");
        }
    }

    /**
     * 删除资源
     * @param resourceId 资源ID
     * @return 删除结果
     */
    @Override
    public Result<?> deleteResource(Long resourceId) {
        try {
            SysResource resource = sysResourceMapper.selectById(resourceId);
            if (resource == null) {
                return Result.failed(400, "资源不存在");
            }

            // 检查资源是否有子节点
            int childCount = Math.toIntExact(sysResourceMapper.selectCount(
                    new LambdaQueryWrapper<SysResource>()
                            .eq(SysResource::getParentId, resourceId)));
            if (childCount > 0) {
                return Result.failed(400, "该资源下有子节点, 无法删除");
            }

            // 删除资源
            boolean deleted = removeById(resourceId);
            if (deleted) {
                return Result.success("资源删除成功");
            } else {
                return Result.failed(500, "资源删除失败");
            }
        } catch (Exception e) {
            log.error("资源删除失败", e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "资源删除失败");
        }
    }
}
