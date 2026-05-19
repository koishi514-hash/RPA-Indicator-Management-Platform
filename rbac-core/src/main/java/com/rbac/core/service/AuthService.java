package com.rbac.core.service;

import com.rbac.common.model.dto.LoginRequest;
import com.rbac.common.model.vo.LoginVO;
import com.rbac.common.response.Result;

/**
 * 认证服务接口
 */

public interface AuthService {

    /**
     * 登录
     */
    Result<LoginVO> login(LoginRequest loginRequest);

    /**
     * 退出登录
     */
    Result<?> logout();

    /**
     * 获取当前用户信息
     */
    Result<?> getCurrentUserInfo();
}
