package com.rbac.common.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 更新用户请求DTO
 */

@Data
public class UpdateUserRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "显示名称长度不能超过64个字符")
    private String nickname;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱格式")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 128, message = "手机号长度不能超过128个字符")
    private String phone;

    @NotEmpty(message = "角色ID不能为空")
    private List<Long> roleIds;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
