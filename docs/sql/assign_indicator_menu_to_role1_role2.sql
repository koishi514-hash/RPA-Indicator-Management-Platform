-- ================================================
-- 将指标管理模块菜单权限分配给 role_id=1 和 role_id=2
-- 注意：此文件假设资源已存在，只用于分配权限
-- ================================================

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
-- 查询分配结果
-- ----------------------------

SELECT '成功将指标管理菜单权限分配给 role_id=1 和 role_id=2！' AS message;

SELECT 
    res.id,
    res.resource_code,
    res.resource_name,
    CASE res.resource_type WHEN 1 THEN '菜单' WHEN 2 THEN '按钮' WHEN 3 THEN '接口' END AS resource_type,
    res.path,
    CASE WHEN rr1.role_id IS NOT NULL THEN '✓' ELSE '' END AS has_role1,
    CASE WHEN rr2.role_id IS NOT NULL THEN '✓' ELSE '' END AS has_role2
FROM `sys_resource` res
LEFT JOIN `sys_role_resource` rr1 ON res.id = rr1.resource_id AND rr1.role_id = 1
LEFT JOIN `sys_role_resource` rr2 ON res.id = rr2.resource_id AND rr2.role_id = 2
WHERE res.resource_code IN ('INDICATOR', 'INDICATOR_CALCULATION', 'INDICATOR_AUDIT')
ORDER BY res.id;
