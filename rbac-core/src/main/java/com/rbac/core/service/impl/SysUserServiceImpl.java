package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddUserRequest;
import com.rbac.common.model.dto.UpdatePasswordRequest;
import com.rbac.common.model.dto.UpdateCurrentUserRequest;
import com.rbac.common.model.dto.UpdateUserRequest;
import com.rbac.common.response.Result;
import com.rbac.common.utils.PasswordUtils;
import com.rbac.core.domain.entity.SysRole;
import com.rbac.core.domain.entity.SysUser;
import com.rbac.core.domain.entity.SysUserRole;
import com.rbac.core.domain.mapper.SysUserMapper;
import com.rbac.core.domain.mapper.SysUserRoleMapper;
import com.rbac.core.service.SysRoleService;
import com.rbac.core.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleService sysRoleService;

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public SysUser getByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        log.info("查询用户: {}", username);
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getStatus, 1));
        log.info("查询结果: {}", user);
        return user;
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @Override
    public Result<?> getProfile(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.failed(401, "未登录或登录状态已过期");
        }

        var roleNames = sysRoleService.getRoleNamesByUserId(user.getId());
        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("username", user.getUsername());
        responseData.put("nickname", user.getNickname());
        responseData.put("email", user.getEmail());
        responseData.put("phone", user.getPhone());
        responseData.put("roleNames", roleNames);
        responseData.put("avatarUrl", user.getAvatarUrl());
        responseData.put("status", user.getStatus());
        responseData.put("createTime", user.getCreateTime());
        // 构建个人信息响应
        return Result.success("success", responseData);
    }

    /**
     * 更新当前登录用户信息
     * @param request 更新当前登录用户信息请求
     * @param session 会话
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateProfile(UpdateCurrentUserRequest request, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.failed(401, "未登录或登录状态已过期");
        }

        // 更新用户信息
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        try{
            // 保存更新
            boolean updated = updateById(user);
            if (updated) {
                // 更新会话中的用户信息
                session.setAttribute("user", user);
                return Result.success("个人信息更新成功");
            } else {
                return Result.failed(500, "个人信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新个人信息失败: {}", e.getMessage(), e);
            return Result.failed(500, "个人信息更新失败");
        }
    }

    /**
     * 更新用户密码
     * @param request 更新密码请求
     * @param session 会话
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updatePassword(UpdatePasswordRequest request, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.failed(401, "未登录或登录状态已过期");
        }

        // 验证旧密码
        if(!PasswordUtils.matches(request.getOldPassword(), user.getPassword())) {
            return Result.failed(400, "旧密码错误");
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Result.failed(400, "新密码和确认密码不一致");
        }

        // 加密新密码
        String newPasswordHash = PasswordUtils.encode(request.getNewPassword());
        user.setPassword(newPasswordHash);

        try{
            // 保存更新
            boolean updated = updateById(user);
            if (updated) {
                // 更新会话中的用户信息
                session.setAttribute("user", user);
                return Result.success("密码更新成功");
            } else {
                return Result.failed(500, "密码更新失败");
            }
        } catch (Exception e) {
            log.error("更新密码失败: {}", e.getMessage(), e);
            return Result.failed(500, "密码更新失败");
        }
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public Result<?> pageUserList(String username, String nickname, String roleCode, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(SysUser::getUsername, username);
        }
        if (nickname != null && !nickname.isEmpty()) {
            queryWrapper.like(SysUser::getNickname, nickname);
        }

        // 通过 roleCode 查询用户
        if (roleCode != null && !roleCode.isEmpty()) {
            // 查询具有指定角色代码的用户ID列表
            List<Long> userIds = sysRoleService.getUserIdsByRoleCode(roleCode);
            if (!userIds.isEmpty()) {
                queryWrapper.in(SysUser::getId, userIds);
            } else {
                // 如果没有符合条件的用户，返回空结果
                Page<Map<String, Object>> emptyPage = new Page<>(pageNum, pageSize);
                emptyPage.setTotal(0);
                emptyPage.setRecords(new ArrayList<>());
                return Result.success("success", emptyPage);
            }
        }

        // 分页查询
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        // 处理用户数据
        List<Map<String, Object>> records = new ArrayList<>();
        List<SysUser> userList = page.getRecords();
        if (userList != null && !userList.isEmpty()) {
            for (SysUser user : userList) {
                Map<String, Object> record = new HashMap<>();
                record.put("userId", user.getId());
                record.put("username", user.getUsername());
                record.put("nickname", user.getNickname());
                record.put("email", user.getEmail());
                record.put("phone", user.getPhone());
                List<SysRole> roleList = sysRoleService.getRolesByUserId(user.getId());
                record.put("roleIds", roleList != null && !roleList.isEmpty() ? roleList.get(0).getId() : "");
                List<String> roleNameList = sysRoleService.getRoleNamesByUserId(user.getId());
                record.put("roleNames", roleNameList != null && !roleNameList.isEmpty() ? roleNameList.get(0) : "");
                List<String> roleCodeList = sysRoleService.getRoleCodesByUserId(user.getId());
                record.put("roleCodes", roleCodeList != null && !roleCodeList.isEmpty() ? roleCodeList.get(0) : "");
                record.put("status", user.getStatus());
                record.put("createTime", user.getCreateTime());
                record.put("updateTime", user.getUpdateTime());
                records.add(record);
            }
        }
        // 构建分页结果
        Page<Map<String, Object>> pageResult = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        pageResult.setRecords(records);
        // 构建响应数据
        return Result.success("success", pageResult);
    }

    /**
     * 获取单个用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    @Override
    public Result<?> getUserDetail(Long userId) {
        if (userId == null) {
            return Result.failed(400, "用户ID不能为空");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.failed(404, "用户不存在");
        }
        // 获取用户角色ID列表
        List<Long> roleIds = new ArrayList<>();
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        if (userRoles != null) {
            for (SysUserRole userRole : userRoles) {
                roleIds.add(userRole.getRoleId());
            }
        }
        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("username", user.getUsername());
        responseData.put("nickname", user.getNickname());
        responseData.put("email", user.getEmail());
        responseData.put("phone", user.getPhone());
        responseData.put("avatarUrl", user.getAvatarUrl());
        responseData.put("status", user.getStatus());
        responseData.put("roleIds", roleIds);
        List<String> roleNames = sysRoleService.getRoleNamesByUserId(userId);
        responseData.put("roleNames", roleNames != null && !roleNames.isEmpty() ? roleNames.get(0) : "");
        responseData.put("createTime", user.getCreateTime());
        responseData.put("updateTime", user.getUpdateTime());

        return Result.success("success", responseData);
    }

    /**
     * 新增用户
     * @param request 新增用户请求
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addUser(AddUserRequest request) {
        // 检查用户名是否已存在
        SysUser existingUser = getByUsername(request.getUsername());
        if (existingUser != null) {
            return Result.failed(400, "用户名已存在");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        // 默认启用
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        try {
            // 保存用户
            boolean saved = save(user);
            if (!saved) {
                return Result.failed(500, "用户新增失败");
            }
            // 关联角色
            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
                List<String> addRole = new ArrayList<>();
                for (Long roleId : request.getRoleIds()) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(roleId);
                    userRole.setCreateTime(LocalDateTime.now());
                    sysUserRoleMapper.insert(userRole);

                    // 获取角色信息并将角色编码添加到user表中
                    SysRole role = sysRoleService.getById(roleId);
                    if (role != null) {
                        addRole.add(String.valueOf(role.getRoleCode()));
                    }
                }
                // 设置角色编码到user实体的roleNames字段
                if (!addRole.isEmpty()) {
                    user.setRoleNames(String.join(",", addRole));
                    updateById(user);
                }
            }
            return Result.success("用户新增成功");
        } catch (Exception e) {
            log.error("新增用户失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "用户新增失败");
        }
    }

    /**
     * 更新用户信息
     * @param request 更新用户信息请求
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateUser(Long userId, UpdateUserRequest request) {
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return Result.failed(404, "用户不存在");
            }

            // 更新用户信息
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            user.setUpdateTime(LocalDateTime.now());
            // 更新用户角色
            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
                // 删除旧的角色关联
                sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));

                // 添加新的角色关联
                for (Long roleId : request.getRoleIds()) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCreateTime(LocalDateTime.now());
                    sysUserRoleMapper.insert(userRole);
                }
            }

            // 保存更新
            boolean updated = updateById(user);
            if (updated) {
                return Result.success("用户信息更新成功");
            } else {
                return Result.failed(500, "用户信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "用户信息更新失败");
        }
    }

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 重置结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> resetPassword(Long userId, String newPassword) {
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return Result.failed(404, "用户不存在");
            }
            // 重置密码, 若提供了新密码则使用新密码, 否则使用默认密码123456
            String password = newPassword != null ? newPassword : "123456";
            String passwordHash = PasswordUtils.encode(password);
            user.setPassword(passwordHash);
            user.setUpdateTime(LocalDateTime.now());

            // 保存更新
            boolean updated = updateById(user);
            if (updated) {
                return Result.success("用户重置密码成功");
            } else {
                return Result.failed(500, "用户重置密码失败");
            }
        } catch (Exception e) {
            log.error("重置用户密码失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "重置用户密码失败");
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteUser(Long userId) {
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return Result.failed(404, "用户不存在");
            }

            // 删除用户头像文件
            if (user.getAvatarUrl() != null) {
                String avatarUrl = user.getAvatarUrl();
                // 从URL中提取文件名
                String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                String avatarPath = uploadPath + "avatar/" + fileName;
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    boolean deleted = avatarFile.delete();
                    if (!deleted) {
                        log.warn("删除用户头像文件失败: {}", avatarPath);
                    }
                }
            }

            // 删除用户角色关联
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, userId));

            // 删除用户
            boolean deleted = removeById(userId);
            if (deleted) {
                return Result.success("用户删除成功");
            } else {
                return Result.failed(500, "用户删除失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "用户删除失败");
        }
    }

    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 禁用结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> disableUser(Long userId) {
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return Result.failed(404, "用户不存在");
            }
            user.setStatus(0);
            user.setUpdateTime(LocalDateTime.now());
            boolean updated = updateById(user);
            if (updated) {
                return Result.success("用户禁用成功");
            } else {
                return Result.failed(500, "用户禁用失败");
            }
        } catch (Exception e) {
            log.error("禁用用户失败: {}", e.getMessage(), e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "禁用用户失败");
        }
    }
}
