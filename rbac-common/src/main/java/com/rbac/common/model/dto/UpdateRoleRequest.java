package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 更新角色请求DTO
 */

@Data
public class UpdateRoleRequest {

//    @NotBlank(message = "角色名称不能为空")
    private String roleName;

//    @NotEmpty(message = "资源ID列表不能为空")
    private List<Long> resourceIds;

//    @NotBlank(message = "角色描述不能为空")
    private String description;

    private Integer status;
}
