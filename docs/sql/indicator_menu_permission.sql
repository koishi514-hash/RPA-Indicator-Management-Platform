-- ================================================
-- 指标管理模块菜单资源（基于前端路由）
-- ================================================

-- ----------------------------
-- 添加菜单资源（id自增，不需要手动指定）
-- ----------------------------

-- 指标管理父菜单
INSERT INTO `sys_resource` (`resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES ('INDICATOR', '指标管理', 1, 0, '/indicator', 'indicator', 3, 1, NOW());

-- 指标计算子菜单
INSERT INTO `sys_resource` (`resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES ('INDICATOR_CALCULATION', '指标计算', 1, (SELECT @parent_id := id FROM (SELECT id FROM sys_resource WHERE resource_code = 'INDICATOR') t), '/indicator/calculation', 'calculator', 0, 1, NOW());

-- 指标审核子菜单
INSERT INTO `sys_resource` (`resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES ('INDICATOR_AUDIT', '指标审核', 1, (SELECT @parent_id := id FROM (SELECT id FROM sys_resource WHERE resource_code = 'INDICATOR') t), '/indicator/audit', 'audit', 1, 1, NOW());

-- ----------------------------
-- 将权限分配给管理员角色（role_id=1）
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 1, id, NOW() FROM `sys_resource` WHERE resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT');

-- ----------------------------
-- 将权限分配给普通用户角色（role_id=2）
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 2, id, NOW() FROM `sys_resource` WHERE resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT');

-- ----------------------------
-- 查询添加结果
-- ----------------------------

SELECT '成功添加指标管理菜单资源并分配给 role_id=1 和 role_id=2！' AS message;

SELECT 
    res.id,
    res.resource_code,
    res.resource_name,
    CASE res.resource_type WHEN 1 THEN '菜单' WHEN 2 THEN '按钮' WHEN 3 THEN '接口' END AS resource_type,
    res.parent_id,
    res.path
FROM `sys_resource` res
WHERE res.resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT')
ORDER BY res.id;
