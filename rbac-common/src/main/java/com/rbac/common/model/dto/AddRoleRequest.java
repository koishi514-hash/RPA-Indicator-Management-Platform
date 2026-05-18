package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 添加系统角色请求DTO
 */

@Data
public class AddRoleRequest {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色描述不能为空")
    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotEmpty(message = "资源ID不能为空")
    private List<Long> resourceIds;
   }
