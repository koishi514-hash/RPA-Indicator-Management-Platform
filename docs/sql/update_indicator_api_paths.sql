-- ================================================
-- 更新指标管理接口资源路径（匹配 Controller 实际路径）
-- ================================================

-- ----------------------------
-- 更新接口资源路径（改为与 Controller 一致）
-- 注意：Controller 路径是 /api/v1/indicator/calculation
-- ----------------------------

-- 更新指标列表接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/list' WHERE `resource_code` = 'API_INDICATOR_LIST';

-- 更新新增指标接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/create' WHERE `resource_code` = 'API_INDICATOR_CREATE';

-- 更新更新指标接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/update' WHERE `resource_code` = 'API_INDICATOR_UPDATE';

-- 更新删除指标接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/delete/*' WHERE `resource_code` = 'API_INDICATOR_DELETE';

-- 更新指标详情接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/detail/*' WHERE `resource_code` = 'API_INDICATOR_DETAIL';

-- 更新所有指标接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/calculation/all' WHERE `resource_code` = 'API_INDICATOR_ALL';

-- 更新审核规则列表接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/list' WHERE `resource_code` = 'API_QUOTA_RULE_LIST';

-- 更新新增审核规则接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/create' WHERE `resource_code` = 'API_QUOTA_RULE_CREATE';

-- 更新更新审核规则接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/update' WHERE `resource_code` = 'API_QUOTA_RULE_UPDATE';

-- 更新删除审核规则接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/delete/*' WHERE `resource_code` = 'API_QUOTA_RULE_DELETE';

-- 更新审核规则详情接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/detail/*' WHERE `resource_code` = 'API_QUOTA_RULE_DETAIL';

-- 更新执行指标审核接口
UPDATE `sys_resource` SET `path` = '/api/v1/indicator/audit/calculate' WHERE `resource_code` = 'API_QUOTA_RULE_CALCULATE';

-- ----------------------------
-- 查询验证结果
-- ----------------------------

SELECT '成功更新指标管理接口资源路径！' AS message;

SELECT 
    id,
    resource_code,
    resource_name,
    path
FROM `sys_resource` 
WHERE `path` LIKE '/api/v1/indicator/%'
ORDER BY id;
