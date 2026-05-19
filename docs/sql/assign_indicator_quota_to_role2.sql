-- ================================================
-- 将指标管理和指标审核权限分配给普通用户角色（role_id为2）
-- 注意：此文件假设资源数据已通过 permission_indicator_quota.sql 创建
-- ================================================

-- ----------------------------
-- 将指标管理和指标审核相关权限分配给 role_id = 2（普通用户）
-- ----------------------------

-- 分配菜单资源
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 31, NOW());  -- 指标管理菜单
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 32, NOW());  -- 指标审核菜单

-- 分配指标管理按钮权限
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 33, NOW());  -- 指标列表
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 34, NOW());  -- 新增指标
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 35, NOW());  -- 编辑指标
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 36, NOW());  -- 删除指标
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 37, NOW());  -- 查看指标

-- 分配指标审核按钮权限
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 38, NOW());  -- 审核规则列表
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 39, NOW());  -- 新增审核规则
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 40, NOW());  -- 编辑审核规则
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 41, NOW());  -- 删除审核规则
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 42, NOW());  -- 查看审核规则
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 43, NOW());  -- 执行指标审核

-- 分配指标管理接口权限
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 44, NOW());  -- 指标列表接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 45, NOW());  -- 新增指标接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 46, NOW());  -- 更新指标接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 47, NOW());  -- 删除指标接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 48, NOW());  -- 指标详情接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 49, NOW());  -- 所有指标接口

-- 分配指标审核接口权限
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 50, NOW());  -- 审核规则列表接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 51, NOW());  -- 新增审核规则接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 52, NOW());  -- 更新审核规则接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 53, NOW());  -- 删除审核规则接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 54, NOW());  -- 审核规则详情接口
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) VALUES (2, 55, NOW());  -- 执行指标审核接口

-- ----------------------------
-- 查询分配结果
-- ----------------------------

SELECT '成功将指标管理和指标审核权限分配给 role_id = 2（普通用户）！' AS message;
SELECT r.role_name, res.resource_name, res.resource_code, res.resource_type
FROM sys_role_resource rr
JOIN sys_role r ON rr.role_id = r.id
JOIN sys_resource res ON rr.resource_id = res.id
WHERE rr.role_id = 2 AND rr.resource_id >= 31
ORDER BY res.resource_type, res.id;
