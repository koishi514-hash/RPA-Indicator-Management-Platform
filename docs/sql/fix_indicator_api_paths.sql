-- ================================================
-- 修复指标管理接口资源路径（匹配 Controller 实际路径）
-- Controller 实际路径: /api/v1/indicators
-- ================================================

-- ----------------------------
-- 更新指标管理接口路径（复数indicators，无calculation）
-- ----------------------------

UPDATE `sys_resource` SET `path` = '/api/v1/indicators/list' WHERE `resource_code` = 'API_INDICATOR_LIST';
UPDATE `sys_resource` SET `path` = '/api/v1/indicators/create' WHERE `resource_code` = 'API_INDICATOR_CREATE';
UPDATE `sys_resource` SET `path` = '/api/v1/indicators/update' WHERE `resource_code` = 'API_INDICATOR_UPDATE';
UPDATE `sys_resource` SET `path` = '/api/v1/indicators/delete/*' WHERE `resource_code` = 'API_INDICATOR_DELETE';
UPDATE `sys_resource` SET `path` = '/api/v1/indicators/detail/*' WHERE `resource_code` = 'API_INDICATOR_DETAIL';
UPDATE `sys_resource` SET `path` = '/api/v1/indicators/all' WHERE `resource_code` = 'API_INDICATOR_ALL';

-- ----------------------------
-- 更新审核规则接口路径（复数quota-rules，无audit）
-- ----------------------------

UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/list' WHERE `resource_code` = 'API_QUOTA_RULE_LIST';
UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/create' WHERE `resource_code` = 'API_QUOTA_RULE_CREATE';
UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/update' WHERE `resource_code` = 'API_QUOTA_RULE_UPDATE';
UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/delete/*' WHERE `resource_code` = 'API_QUOTA_RULE_DELETE';
UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/detail/*' WHERE `resource_code` = 'API_QUOTA_RULE_DETAIL';
UPDATE `sys_resource` SET `path` = '/api/v1/quota-rules/calculate' WHERE `resource_code` = 'API_QUOTA_RULE_CALCULATE';

-- ----------------------------
-- 验证结果
-- ----------------------------

SELECT '成功更新指标管理接口资源路径！' AS message;

SELECT
    id,
    resource_code,
    resource_name,
    path
FROM `sys_resource`
WHERE `path` LIKE '/api/v1/indicators%' OR `path` LIKE '/api/v1/quota-rules%'
ORDER BY id;
