package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加系统资源请求DTO
 */

@Data
public class AddResourceRequest {

    @NotBlank(message = "资源编码不能为空")
    private String resourceCode;

    @NotBlank(message = "资源名称不能为空")
    private String resourceName;

    @NotNull(message = "资源类型不能为空")
    private Integer resourceType;

    @NotNull(message = "父资源ID不能为空")
    private Long parentId;

    private String path;

    private String icon;

    @NotNull(message = "排序不能为空不能为空")
    private Integer sortOrder;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
