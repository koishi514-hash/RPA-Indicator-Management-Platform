-- ================================================
-- 指标管理模块权限数据（基于前端路由配置）
-- ================================================

-- ----------------------------
-- 添加菜单资源
-- ----------------------------

-- 指标管理父菜单
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (56, 'INDICATOR', '指标管理', 1, 0, '/indicator', 'indicator', 3, 1, NOW());

-- 指标计算子菜单
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (57, 'INDICATOR_CALCULATION', '指标计算', 1, 56, '/indicator/calculation', 'calculator', 0, 1, NOW());

-- 指标审核子菜单
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (58, 'INDICATOR_AUDIT', '指标审核', 1, 56, '/indicator/audit', 'audit', 1, 1, NOW());

-- ----------------------------
-- 添加按钮权限
-- ----------------------------

-- 指标计算按钮权限
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (59, 'INDICATOR_CALCULATION_LIST', '指标列表', 2, 57, NULL, 'list', 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (60, 'INDICATOR_CALCULATION_CREATE', '新增指标', 2, 57, NULL, 'plus', 1, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (61, 'INDICATOR_CALCULATION_EDIT', '编辑指标', 2, 57, NULL, 'edit', 2, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (62, 'INDICATOR_CALCULATION_DELETE', '删除指标', 2, 57, NULL, 'delete', 3, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (63, 'INDICATOR_CALCULATION_VIEW', '查看指标', 2, 57, NULL, 'view', 4, 1, NOW());

-- 指标审核按钮权限
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (64, 'INDICATOR_AUDIT_LIST', '审核规则列表', 2, 58, NULL, 'list', 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (65, 'INDICATOR_AUDIT_CREATE', '新增审核规则', 2, 58, NULL, 'plus', 1, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (66, 'INDICATOR_AUDIT_EDIT', '编辑审核规则', 2, 58, NULL, 'edit', 2, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (67, 'INDICATOR_AUDIT_DELETE', '删除审核规则', 2, 58, NULL, 'delete', 3, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (68, 'INDICATOR_AUDIT_VIEW', '查看审核规则', 2, 58, NULL, 'view', 4, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (69, 'INDICATOR_AUDIT_CALCULATE', '执行指标审核', 2, 58, NULL, 'play', 5, 1, NOW());

-- ----------------------------
-- 添加接口权限
-- ----------------------------

-- 指标计算接口
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (70, 'API_INDICATOR_LIST', '指标列表接口', 3, 59, '/api/v1/indicators/list', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (71, 'API_INDICATOR_CREATE', '新增指标接口', 3, 60, '/api/v1/indicators/create', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (72, 'API_INDICATOR_UPDATE', '更新指标接口', 3, 61, '/api/v1/indicators/update', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (73, 'API_INDICATOR_DELETE', '删除指标接口', 3, 62, '/api/v1/indicators/delete/*', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (74, 'API_INDICATOR_DETAIL', '指标详情接口', 3, 63, '/api/v1/indicators/detail/*', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (75, 'API_INDICATOR_ALL', '所有指标接口', 3, 59, '/api/v1/indicators/all', NULL, 0, 1, NOW());

-- 指标审核接口
INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (76, 'API_QUOTA_RULE_LIST', '审核规则列表接口', 3, 64, '/api/v1/quota-rules/list', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (77, 'API_QUOTA_RULE_CREATE', '新增审核规则接口', 3, 65, '/api/v1/quota-rules/create', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (78, 'API_QUOTA_RULE_UPDATE', '更新审核规则接口', 3, 66, '/api/v1/quota-rules/update', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (79, 'API_QUOTA_RULE_DELETE', '删除审核规则接口', 3, 67, '/api/v1/quota-rules/delete/*', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (80, 'API_QUOTA_RULE_DETAIL', '审核规则详情接口', 3, 68, '/api/v1/quota-rules/detail/*', NULL, 0, 1, NOW());

INSERT INTO `sys_resource` (`id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`, `create_time`) 
VALUES (81, 'API_QUOTA_RULE_CALCULATE', '执行指标审核接口', 3, 69, '/api/v1/quota-rules/calculate', NULL, 0, 1, NOW());

-- ----------------------------
-- 将权限分配给管理员角色（role_id=1）
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 1, id, NOW() FROM `sys_resource` WHERE id >= 56;

-- ----------------------------
-- 将权限分配给普通用户角色（role_id=2）
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 2, id, NOW() FROM `sys_resource` WHERE id >= 56;

-- ----------------------------
-- 查询添加结果
-- ----------------------------

SELECT '成功添加指标管理模块权限并分配给 role_id=1 和 role_id=2！' AS message;

SELECT 
    res.id,
    res.resource_code,
    res.resource_name,
    CASE res.resource_type 
        WHEN 1 THEN '菜单'
        WHEN 2 THEN '按钮'
        WHEN 3 THEN '接口'
    END AS resource_type,
    res.parent_id,
    res.path
FROM `sys_resource` res
WHERE res.id >= 56
ORDER BY res.parent_id, res.id;
