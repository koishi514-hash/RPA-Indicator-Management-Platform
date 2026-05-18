-- ================================================
-- 清理并重新添加指标管理菜单资源
-- ================================================

-- ----------------------------
-- 1. 删除 sys_role_resource 表中 id 为 326 到 381 的数据
-- ----------------------------

DELETE FROM `sys_role_resource` WHERE id >= 326 AND id <= 381;

-- ----------------------------
-- 2. 删除 sys_resource 表中 id 为 31 到 55 的数据
-- ----------------------------

DELETE FROM `sys_resource` WHERE id >= 31 AND id <= 55;

-- ----------------------------
-- 3. 添加新的菜单资源（使用前端路由的 path）
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
-- 4. 将权限分配给 role_id=1 和 role_id=2
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 1, id, NOW() FROM `sys_resource` WHERE resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT');

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 2, id, NOW() FROM `sys_resource` WHERE resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT');

-- ----------------------------
-- 5. 查询验证结果
-- ----------------------------

SELECT '清理和添加完成！' AS message;

-- 查看添加的资源
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

-- 查看权限分配
SELECT 
    r.role_name,
    res.resource_name,
    res.resource_code
FROM sys_role_resource rr
JOIN sys_role r ON rr.role_id = r.id
JOIN sys_resource res ON rr.resource_id = res.id
WHERE rr.resource_id IN (SELECT id FROM sys_resource WHERE resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT'))
ORDER BY r.id, res.id;
