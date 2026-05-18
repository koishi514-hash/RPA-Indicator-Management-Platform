package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色编码（唯一）：如 ADMIN, OPERATOR, VIEWER
     */
    private String roleCode;

    /**
     * 角色名称：如 系统管理员、操作员、查看者
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;
}