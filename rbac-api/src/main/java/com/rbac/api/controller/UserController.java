package com.rbac.api.controller;

import com.rbac.common.model.dto.*;
import com.rbac.common.response.Result;
import com.rbac.core.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system")
@Tag(name = "用户管理", description = "用户相关接口, 包括个人信息和密码管理")
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 获取个人信息
     * @param request HTTP请求
     * @return 个人信息
     */
    @GetMapping("profile")
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的详细信息")
    public Result<?> getProfile(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return sysUserService.getProfile(session);
    }

    /**
     * 更新个人信息
     * @param request 更新个人信息请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("profile")
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的详细信息")
    public Result<?> updateProfile(@Validated @RequestBody UpdateCurrentUserRequest request, HttpServletRequest httpRequest ) {
        HttpSession session = httpRequest.getSession();
        return sysUserService.updateProfile(request, session);
    }

    /**
     * 更新密码
     * @param request 更新密码请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("profile/password")
    @Operation(summary = "更新密码", description = "修改当前登录用户的密码")
    public Result<?> updatePassword(@Validated @RequestBody UpdatePasswordRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        return sysUserService.updatePassword(request, session);
    }

    /**
     * 分页查询用户列表
     * @param username 用户名
     * @param nickname 显示名称
     * @param roleCode 角色编码
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @GetMapping("users")
    @Operation(summary = "分页查询用户列表", description = "根据用户名、显示名称、角色编码分页查询用户列表")
    public Result<?> pageUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String roleCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
    return sysUserService.pageUserList(username, nickname, roleCode, pageNum, pageSize);
    }

    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("users/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public Result<?> getUserDetail(@PathVariable Long userId) {
        return sysUserService.getUserDetail(userId);
    }

    /**
     * 新增用户
     * @param request 新增用户请求
     * @return 新增结果
     */
    @PostMapping("users")
    @Operation(summary = "新增用户", description = "根据添加用户请求参数添加用户到数据库")
    public Result<?> addUser(@Validated @RequestBody AddUserRequest request) {
        return sysUserService.addUser(request);
    }

    /**
     * 更新用户
     * @param request 更新用户请求
     * @return 更新结果
     */
    @PutMapping("users/{userId}")
    @Operation(summary = "更新用户", description = "更新一个用户")
    public Result<?> updateUser(@PathVariable Long userId, @Validated @RequestBody UpdateUserRequest request) {
        return sysUserService.updateUser(userId, request);
    }

    /**
     * 重置密码
     * @param userId 用户ID
     * @param request 新密码
     * @return 重置密码结果
     */
    @PutMapping("users/{userId}/password")
    @Operation(summary = "重置密码", description = "重置用户密码")
    public Result<?> resetPassword(@PathVariable Long userId, @Validated @RequestBody(required = false) RestPasswordRequest request) {
        String newPassword = request != null ? request.getNewPassword() : null;
        return sysUserService.resetPassword(userId, newPassword);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("users/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户, 若后端存在用户头像文件, 则一并删除用户头像文件")
    public Result<?> deleteUser(@PathVariable Long userId) {
        return sysUserService.deleteUser(userId);
    }

    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 禁用结果
     */
    @PatchMapping("users/{userId}/status")
    @Operation(summary = "禁用用户", description = "根据用户ID禁用用户")
    public Result<?> disableUser(@PathVariable Long userId) {
        return sysUserService.disableUser(userId);
    }
}
