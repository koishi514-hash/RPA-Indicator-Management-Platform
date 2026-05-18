package com.rbac.common.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新当前用户信息请求DTO
 */

@Data
public class UpdateCurrentUserRequest {

    /**
     * 显示名称
     */
    @Size(max = 64, message = "显示名称长度不能超过64个字符")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "请输入正确的邮箱格式")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    /**
     * 手机号
     */
    @Size(max = 128, message = "手机号长度不能超过128个字符")
    private String phone;

    /**
     * 头像URL
     */
    @Size(max = 256, message = "头像URL长度不能超过256个字符")
    private String avatarUrl;
}
