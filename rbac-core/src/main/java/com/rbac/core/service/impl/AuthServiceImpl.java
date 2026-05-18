package com.rbac.core.service.impl;

import com.rbac.common.model.dto.LoginRequest;
import com.rbac.common.model.vo.LoginVO;
import com.rbac.common.response.Result;
import com.rbac.common.utils.JwtUtils;
import com.rbac.common.utils.PasswordUtils;
import com.rbac.core.domain.entity.SysUser;
import com.rbac.core.service.AuthService;
import com.rbac.core.service.SysResourceService;
import com.rbac.core.service.SysRoleService;
import com.rbac.core.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final HttpServletRequest request;
    private final SysRoleService sysRoleService;
    private final SysResourceService sysResourceService;

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */

    @Override
    public Result<LoginVO> login(LoginRequest loginRequest) {
        // 根据用户名查询用户信息
        SysUser user = sysUserService.getByUsername(loginRequest.getUsername());

        if (user == null) {
            return Result.failed(401, "用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtils.matches(loginRequest.getPassword(), user.getPassword())) {
            return Result.failed(401, "用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != 1) {
            return Result.failed(401, "用户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        sysUserService.updateById(user);

        // 生成JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        String token = JwtUtils.generateToken(user.getId().toString(), claims);

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setLoginId(user.getId().toString());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getNickname());

        // 保存用户信息到会话
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return Result.success("登录成功", loginVO);
    }

    /**
     * 退出登录
     * @return 退出结果
     */

    @Override
    public Result<?> logout() {
        HttpSession session = request.getSession();
        session.invalidate();
        return Result.success("退出登录成功");
    }

    /*
     * 获取当前登录用户信息
     * @return 用户信息
     */

    @Override
    public Result<?> getCurrentUserInfo() {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute("user");

        if (user == null) {
            return Result.failed(401, "未登录或token已过期");
        }

        var roleNames = sysRoleService.getRoleNamesByUserId(user.getId());
        var permissions = sysResourceService.getPermissionsByUserId(user.getId());

        // 构建用户信息响应
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("loginId", user.getId().toString());
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getNickname());
        userInfo.put("email", user.getEmail());
        userInfo.put("roleNames", roleNames);
        userInfo.put("permissions", permissions);
        return Result.success("success", userInfo);
    }
}
