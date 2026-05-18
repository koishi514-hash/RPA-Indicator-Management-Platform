package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 登录用户名（唯一）
     */
    private String username;

    /**
     * 显示名称 / 真实姓名
     */
    private String nickname;

    /**
     * 密码哈希（推荐 bcrypt 或 argon2）
     */
    @TableField("password_hash")
    private String password;

    /**
     * 邮箱（建议唯一）
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 冗余字段：角色名称列表（逗号分隔，用于列表快速展示）
     */
    @TableField("role_names")
    private String roleNames;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}