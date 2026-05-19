package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求DTO
 */

@Data
public class RestPasswordRequest {

    @NotBlank(message = "新密码不能为空")
    @Size(message = "新密码长度必须在6到20之间", min = 6, max = 20)
    private String newPassword;
}
