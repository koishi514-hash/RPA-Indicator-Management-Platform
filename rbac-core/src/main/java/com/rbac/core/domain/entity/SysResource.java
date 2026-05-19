package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 资源表实体类（菜单/权限点）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resource")
public class SysResource extends BaseEntity {

    /**
     * 资源唯一编码（唯一）：如 HOME, TASK_LIST, RPA_MANAGE
     */
    private String resourceCode;

    /**
     * 资源名称（菜单显示名称）
     */
    private String resourceName;

    /**
     * 资源类型：1=菜单，2=按钮/权限点，3=接口/API
     */
    private Integer resourceType;

    /**
     * 父资源ID，0=顶级菜单
     */
    private Long parentId;

    /**
     * 前端路由路径：如 /home, /task-list
     */
    private String path;

    /**
     * 图标名称（element-plus / iconfont 等）
     */
    private String icon;

    /**
     * 同级排序（数值越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 子资源列表（递归查询）
     */
    @TableField(exist = false)  // 逻辑字段，不对应数据库字段
    private List<SysResource> children;
}