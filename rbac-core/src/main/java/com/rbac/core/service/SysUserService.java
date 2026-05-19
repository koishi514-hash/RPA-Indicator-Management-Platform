package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddUserRequest;
import com.rbac.common.model.dto.UpdatePasswordRequest;
import com.rbac.common.model.dto.UpdateCurrentUserRequest;
import com.rbac.common.model.dto.UpdateUserRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysUser;
import jakarta.servlet.http.HttpSession;

/**
 * 系统用户服务接口
 */

public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    Result<?> getProfile(HttpSession session);

    /**
     * 更新用户信息
     * @param request 更新用户信息请求
     * @param session 会话
     * @return 更新结果
     */
    Result<?> updateProfile(UpdateCurrentUserRequest request, HttpSession session);

    /**
     * 更新用户密码
     * @param request 更新密码请求
     * @param session 会话
     * @return 更新结果
     */
    Result<?> updatePassword(UpdatePasswordRequest request, HttpSession session);

    /**
     * 分页查询用户列表
     * @param username 用户名查询参数
     * @param nickname 昵称查询参数
     * @param roleCode 角色编码查询参数
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Result<?> pageUserList(String username, String nickname, String roleCode, Integer pageNum, Integer pageSize);

    /**
     * 获取单个用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    Result<?> getUserDetail(Long userId);

    /**
     * 新增用户
     * @param request 新增用户请求
     * @return 新增结果
     */
    Result<?> addUser(AddUserRequest request);

    /**
     * 更新用户信息
     * @param request 更新用户信息请求
     * @return 更新结果
     */
    Result<?> updateUser(Long userId, UpdateUserRequest request);

    /**
     * 重置用户密码
     */
    Result<?> resetPassword(Long userId, String newPassword);

    /**
     * 删除用户
     */
    Result<?> deleteUser(Long userId);

    /**
     * 禁用用户
     */
    Result<?> disableUser(Long userId);
}
