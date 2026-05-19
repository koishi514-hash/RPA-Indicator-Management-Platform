-- ================================================
-- 指标管理和指标审核权限数据
-- ================================================

-- ----------------------------
-- 添加菜单资源
-- ----------------------------

-- 指标管理菜单
INSERT INTO `sys_resource` VALUES (31, 'INDICATOR', '指标管理', 1, 0, '/indicator', 'indicator', 3, 1, '2026-05-15 10:00:00', NULL);

-- 指标审核菜单
INSERT INTO `sys_resource` VALUES (32, 'QUOTA_AUDIT', '指标审核', 1, 0, '/quota-audit', 'audit', 4, 1, '2026-05-15 10:00:00', NULL);

-- ----------------------------
-- 指标管理菜单下的按钮权限
-- ----------------------------

-- 指标列表
INSERT INTO `sys_resource` VALUES (33, 'INDICATOR_LIST', '指标列表', 2, 31, NULL, 'list', 0, 1, '2026-05-15 10:00:00', NULL);

-- 新增指标
INSERT INTO `sys_resource` VALUES (34, 'INDICATOR_CREATE', '新增指标', 2, 31, NULL, 'plus', 1, 1, '2026-05-15 10:00:00', NULL);

-- 编辑指标
INSERT INTO `sys_resource` VALUES (35, 'INDICATOR_EDIT', '编辑指标', 2, 31, NULL, 'edit', 2, 1, '2026-05-15 10:00:00', NULL);

-- 删除指标
INSERT INTO `sys_resource` VALUES (36, 'INDICATOR_DELETE', '删除指标', 2, 31, NULL, 'delete', 3, 1, '2026-05-15 10:00:00', NULL);

-- 查看指标详情
INSERT INTO `sys_resource` VALUES (37, 'INDICATOR_VIEW', '查看指标', 2, 31, NULL, 'view', 4, 1, '2026-05-15 10:00:00', NULL);

-- ----------------------------
-- 指标审核菜单下的按钮权限
-- ----------------------------

-- 审核规则列表
INSERT INTO `sys_resource` VALUES (38, 'QUOTA_RULE_LIST', '审核规则列表', 2, 32, NULL, 'list', 0, 1, '2026-05-15 10:00:00', NULL);

-- 新增审核规则
INSERT INTO `sys_resource` VALUES (39, 'QUOTA_RULE_CREATE', '新增审核规则', 2, 32, NULL, 'plus', 1, 1, '2026-05-15 10:00:00', NULL);

-- 编辑审核规则
INSERT INTO `sys_resource` VALUES (40, 'QUOTA_RULE_EDIT', '编辑审核规则', 2, 32, NULL, 'edit', 2, 1, '2026-05-15 10:00:00', NULL);

-- 删除审核规则
INSERT INTO `sys_resource` VALUES (41, 'QUOTA_RULE_DELETE', '删除审核规则', 2, 32, NULL, 'delete', 3, 1, '2026-05-15 10:00:00', NULL);

-- 查看审核规则详情
INSERT INTO `sys_resource` VALUES (42, 'QUOTA_RULE_VIEW', '查看审核规则', 2, 32, NULL, 'view', 4, 1, '2026-05-15 10:00:00', NULL);

-- 执行指标审核（额度计算）
INSERT INTO `sys_resource` VALUES (43, 'QUOTA_RULE_CALCULATE', '执行指标审核', 2, 32, NULL, 'calculator', 5, 1, '2026-05-15 10:00:00', NULL);

-- ----------------------------
-- 添加接口权限
-- ----------------------------

-- 指标管理接口
INSERT INTO `sys_resource` VALUES (44, 'API_INDICATOR_LIST', '指标列表接口', 3, 33, '/api/v1/indicators/list', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (45, 'API_INDICATOR_CREATE', '新增指标接口', 3, 34, '/api/v1/indicators/create', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (46, 'API_INDICATOR_UPDATE', '更新指标接口', 3, 35, '/api/v1/indicators/update', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (47, 'API_INDICATOR_DELETE', '删除指标接口', 3, 36, '/api/v1/indicators/delete/*', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (48, 'API_INDICATOR_DETAIL', '指标详情接口', 3, 37, '/api/v1/indicators/detail/*', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (49, 'API_INDICATOR_ALL', '所有指标接口', 3, 33, '/api/v1/indicators/all', NULL, 0, 1, '2026-05-15 10:00:00', NULL);

-- 指标审核接口
INSERT INTO `sys_resource` VALUES (50, 'API_QUOTA_RULE_LIST', '审核规则列表接口', 3, 38, '/api/v1/quota-rules/list', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (51, 'API_QUOTA_RULE_CREATE', '新增审核规则接口', 3, 39, '/api/v1/quota-rules/create', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (52, 'API_QUOTA_RULE_UPDATE', '更新审核规则接口', 3, 40, '/api/v1/quota-rules/update', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (53, 'API_QUOTA_RULE_DELETE', '删除审核规则接口', 3, 41, '/api/v1/quota-rules/delete/*', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (54, 'API_QUOTA_RULE_DETAIL', '审核规则详情接口', 3, 42, '/api/v1/quota-rules/detail/*', NULL, 0, 1, '2026-05-15 10:00:00', NULL);
INSERT INTO `sys_resource` VALUES (55, 'API_QUOTA_RULE_CALCULATE', '执行指标审核接口', 3, 43, '/api/v1/quota-rules/calculate', NULL, 0, 1, '2026-05-15 10:00:00', NULL);

-- ----------------------------
-- 将权限分配给管理员角色和普通用户角色（role_id为1和2）
-- ----------------------------

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 1, id, NOW() FROM `sys_resource` WHERE id >= 31;

INSERT INTO `sys_role_resource` (`role_id`, `resource_id`, `create_time`) 
SELECT 2, id, NOW() FROM `sys_resource` WHERE id >= 31;

-- ----------------------------
-- 查询添加结果
-- ----------------------------

SELECT '成功添加指标管理和指标审核相关权限！' AS message;
SELECT * FROM `sys_resource` WHERE id >= 31;
