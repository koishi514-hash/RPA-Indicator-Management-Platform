package com.rbac.common.model.dto;

import lombok.Data;

/**
 * 更新资源请求DTO
 */

@Data
public class UpdateResourceRequest {

    private Long parentId;

    private String resourceName;

    private Integer resourceType;

    private String path;

    private String icon;

    private Integer sortOrder;

    private Integer status;
}
