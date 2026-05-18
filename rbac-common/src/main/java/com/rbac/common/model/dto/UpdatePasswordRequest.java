package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新密码请求DTO
 */

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度必须在6到32个字符之间")
    private String newPassword;

    @NotBlank(message = "请重新输入新密码")
    private String confirmPassword;
}
