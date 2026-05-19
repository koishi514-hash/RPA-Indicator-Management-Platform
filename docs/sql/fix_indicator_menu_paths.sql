-- ================================================
-- 修复指标管理菜单路径（匹配前端路由）
-- ================================================

-- 1. 更新父菜单路径
UPDATE `sys_resource` SET `path` = '/indicator' WHERE `resource_code` = 'INDICATOR';

-- 2. 更新指标审核菜单路径（前端路由是 /indicator/audit，不是 /quota-audit）
UPDATE `sys_resource` SET `path` = '/indicator/audit' WHERE `resource_code` = 'QUOTA_AUDIT';

-- 3. 添加指标计算子菜单（如果不存在）
INSERT INTO `sys_resource` (`resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`)
SELECT 'INDICATOR_CALCULATION', '指标计算', 1, id, '/indicator/calculation', 'calculator', 0, 1, NOW()
FROM `sys_resource` WHERE `resource_code` = 'INDICATOR'
ON DUPLICATE KEY UPDATE `path` = '/indicator/calculation';

-- 4. 确保所有菜单权限分配给角色1和2
INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`)
SELECT 1, id, NOW() FROM `sys_resource` WHERE `resource_code` IN ('INDICATOR', 'QUOTA_AUDIT', 'INDICATOR_CALCULATION')
ON DUPLICATE KEY UPDATE `create_time` = NOW();

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`)
SELECT 2, id, NOW() FROM `sys_resource` WHERE `resource_code` IN ('INDICATOR', 'QUOTA_AUDIT', 'INDICATOR_CALCULATION')
ON DUPLICATE KEY UPDATE `create_time` = NOW();

-- 5. 验证结果
SELECT 
    id,
    resource_code,
    resource_name,
    resource_type,
    path
FROM `sys_resource`
WHERE `resource_code` IN ('INDICATOR', 'QUOTA_AUDIT', 'INDICATOR_CALCULATION')
ORDER BY id;
