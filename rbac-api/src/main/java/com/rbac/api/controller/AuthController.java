package com.rbac.api.controller;

import com.rbac.common.model.dto.LoginRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证管理控制器
 */

@RestController
@RequestMapping("api/v1/system")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、退出登录、获取用户信息等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("login")
    @Operation(summary = "用户登录", description = "用户通过用户名和密码进行登录，登录成功后返回token信息")
    public Result<?> login(@Validated @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("logout")
    @Operation(summary = "退出登录", description = "用户退出登录，使当前token失效")
    public Result<?> logout() {
        return authService.logout();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("auth/info")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的会话信息（用于验证token有效性）")
    public Result<?> getCurrentUserInfo() {
        return authService.getCurrentUserInfo();
    }
}
