package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddRoleRequest;
import com.rbac.common.model.dto.UpdateRoleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysResource;
import com.rbac.core.domain.entity.SysRole;
import com.rbac.core.domain.entity.SysRoleResource;
import com.rbac.core.domain.entity.SysUserRole;
import com.rbac.core.domain.mapper.SysRoleMapper;
import com.rbac.core.domain.mapper.SysRoleResourceMapper;
import com.rbac.core.domain.mapper.SysUserRoleMapper;
import com.rbac.core.service.SysResourceService;
import com.rbac.core.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现类
 */



@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleResourceMapper sysRoleResourceMapper;
    private final SysResourceService sysResourceService;
    private final SysRoleMapper sysRoleMapper;

    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (userRoles == null || userRoles.isEmpty()) {
            return List.of();
        }

        // 提取角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色信息
        List<SysRole> roles = baseMapper.selectBatchIds(roleIds);
        return roles != null ? roles : List.of();
    }

    /**
     * 根据用户ID查询角色编码列表
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        List<SysRole> roles = getRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID查询角色名称列表
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Override
    public List<String> getRoleNamesByUserId(Long userId) {
        List<SysRole> roles = getRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(SysRole::getRoleName)
                .collect(Collectors.toList());
    }

    /**
     * 根据角色编码查询用户ID列表
     * @param roleCode 角色编码
     * @return 用户ID列表
     */
    @Override
    public List<Long> getUserIdsByRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            return new ArrayList<>();
        }
        // 查询角色信息
        SysRole role = getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));

        if (role == null) {
            return new ArrayList<>();
        }

        // 查询角色-用户关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, role.getId()));

        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取用户ID
        return userRoles.stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询角色列表
     * @param roleCode 角色编码
     * @param roleName 角色名称
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 角色列表
     */
    @Override
    public Result<?> pageRoleList(String roleCode, String roleName, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if (roleCode != null && !roleCode.isEmpty()) {
            queryWrapper.like(SysRole::getRoleCode, roleCode);
        }
        if (roleName != null && !roleName.isEmpty()) {
            queryWrapper.like(SysRole::getRoleName, roleName);
        }

        // 分页查询
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        // 处理角色数据
        List<Map<String, Object>> roleList = new ArrayList<>();
        List<SysRole> roleRecords = page.getRecords();
        if (roleRecords != null && !roleRecords.isEmpty()) {
            for (SysRole role : roleRecords) {
                Map<String, Object> roleMap = new HashMap<>();
                // 添加角色基本信息
                roleMap.put("roleId", role.getId());
                roleMap.put("roleCode", role.getRoleCode());
                roleMap.put("roleName", role.getRoleName());
                roleMap.put("description", role.getDescription());
                roleMap.put("status", role.getStatus());
                roleMap.put("createTime", role.getCreateTime());
                roleMap.put("updateTime", role.getUpdateTime());
                // 根据角色ID获取资源ID列表
                roleMap.put("resourceIds", sysResourceService.getResourcesByRoleIds(List.of(role.getId()))
                        .stream().map(SysResource::getId)
                        .collect(Collectors.toList()));
                // 根据角色ID获取资源名称列表
                roleMap.put("resourceNames", sysResourceService.getResourcesByRoleIds(List.of(role.getId()))
                        .stream().map(SysResource::getResourceName)
                        .collect(Collectors.toList()));
                // 统计用户数量
                int userCount = Math.toIntExact(sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, role.getId())));
                roleMap.put("userCount", userCount);
                roleList.add(roleMap);
            }
        }
        // 创建分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", roleList);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());
        return Result.success(result);
    }

    /**
     * 新建角色
     * @param request 角色新建请求
     * @return 角色新建结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRole(AddRoleRequest request) {
        // 检查角色编码是否已存在
        SysRole role = baseMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, request.getRoleCode()));
        if (role != null) {
            return Result.failed(400, "角色编码已存在");
        }
        // 检查角色名称是否已存在
        SysRole existingRoleName = baseMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, request.getRoleName()));
        if (existingRoleName != null) {
            return Result.failed(400, "角色名称已存在");
        }

        try{
            // 创建新角色
            SysRole newRole = new SysRole();
            newRole.setRoleCode(request.getRoleCode());
            newRole.setRoleName(request.getRoleName());
            newRole.setDescription(request.getDescription());
            // 状态默认启用
            newRole.setStatus(request.getStatus() != null ? request.getStatus() : 1);
            newRole.setCreateTime(LocalDateTime.now());
            newRole.setUpdateTime(LocalDateTime.now());

            // 保存角色
            boolean saved = save(newRole);
            if (!saved) {
                return Result.failed(500, "角色创建失败");
            }
            // 关联资源
            if (request.getResourceIds() != null && ! request.getResourceIds().isEmpty()) {
                for (Long resourceId : request.getResourceIds()) {
                    // 验证资源是否存在
                    if (sysResourceService.getById(resourceId) != null) {
                        SysRoleResource roleResource = new SysRoleResource();
                        roleResource.setRoleId(newRole.getId());
                        roleResource.setResourceId(resourceId);
                        roleResource.setCreateTime(LocalDateTime.now());
                        sysRoleResourceMapper.insert(roleResource);
                    }
                }
            }
            return Result.success("角色新建成功");
        } catch (Exception e) {
            log.error("新建角色失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "角色新建失败");
        }
    }

    /**
     * 更新角色(权限分配)
     * @param request 更新角色请求
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateRole(Long roleId, UpdateRoleRequest request) {
        try{
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role == null) {
                return Result.failed(400, "角色不存在");
            }

            // 更新角色信息
            if (request.getRoleName() != null && !request.getRoleName().isEmpty()) {
                role.setRoleName(request.getRoleName());
            }
            if (request.getDescription() != null && !request.getDescription().isEmpty()) {
                role.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                role.setStatus(request.getStatus());
            }
            role.setUpdateTime(LocalDateTime.now());
            // 更新角色资源
            if (request.getResourceIds() != null && !request.getResourceIds().isEmpty()) {
                // 清空旧资源关联
                sysRoleResourceMapper.delete(new LambdaQueryWrapper<SysRoleResource>()
                        .eq(SysRoleResource::getRoleId, roleId));

                // 添加新的资源关联
                for (Long resourceId : request.getResourceIds()) {
                    SysRoleResource roleResource = new SysRoleResource();
                    roleResource.setRoleId(roleId);
                    roleResource.setResourceId(resourceId);
                    roleResource.setCreateTime(LocalDateTime.now());
                    sysRoleResourceMapper.insert(roleResource);
                }
            }
            // 保存更新
            boolean updated = updateById(role);
            if (updated) {
                return Result.success("角色更新成功");
            } else {
                return Result.failed(500, "角色更新失败");
            }
        } catch (Exception e) {
            log.error("更新角色失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "角色更新失败");
        }
    }

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            return Result.failed(400, "角色不存在");
        }

        // 检查角色是否有用户使用
        int userCount = Math.toIntExact(sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, roleId)));
        if (userCount > 0) {
            return Result.failed(400, "该角色已被用户使用, 无法删除");
        }

        try {
            // 删除角色资源关联
            sysRoleResourceMapper.delete(new LambdaQueryWrapper<SysRoleResource>()
                    .eq(SysRoleResource::getRoleId, roleId));
            // 删除角色
            boolean deleted = removeById(roleId);
            if (deleted) {
                return Result.success("角色删除成功");
            } else {
                return Result.failed(500, "角色删除失败");
            }
        } catch (Exception e) {
            log.error("删除角色失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "角色删除失败");
        }
    }
}
