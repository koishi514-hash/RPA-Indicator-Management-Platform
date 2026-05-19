-- ============================================
-- 指标管理模块数据库脚本
-- 创建日期: 2026-05-14
-- ============================================

-- 1. 指标表
CREATE TABLE IF NOT EXISTS `indicator` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `indicator_name` VARCHAR(100) NOT NULL COMMENT '指标名称',
    `indicator_code` VARCHAR(50) NOT NULL COMMENT '指标编码（唯一）',
    `indicator_logic` VARCHAR(500) NOT NULL COMMENT '指标逻辑描述',
    `task_id` BIGINT NULL COMMENT '关联的任务ID（数据来源）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_indicator_code` (`indicator_code`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标表';

-- 2. 审核规则表
CREATE TABLE IF NOT EXISTS `quota_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `quota_name` VARCHAR(100) NOT NULL COMMENT '额度名称',
    `indicator_codes` VARCHAR(500) NOT NULL COMMENT '指标编码列表，逗号分隔',
    `conditions` VARCHAR(1000) NOT NULL COMMENT '判断条件，分行存储',
    `quota_calculation` VARCHAR(1000) NOT NULL COMMENT '额度计算逻辑',
    `result_var_name` VARCHAR(50) NOT NULL COMMENT '结果变量名称',
    `calculated_result` VARCHAR(100) NULL COMMENT '计算结果',
    `output_template` VARCHAR(1000) NOT NULL COMMENT '输出数据模板',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核规则表';

-- ============================================
-- 示例数据（可选）
-- ============================================

-- 插入示例指标
INSERT INTO `indicator` (`indicator_name`, `indicator_code`, `indicator_logic`, `task_id`) 
VALUES 
('企业税负率', 'TAX_RATE', '税负率 = 纳税额 / 营业收入 * 100%', 1),
('利润率', 'PROFIT_RATE', '利润率 = 利润 / 营业收入 * 100%', 1),
('营业收入', 'REVENUE', '企业总营业收入', 1);

-- 插入示例审核规则
INSERT INTO `quota_rule` (`quota_name`, `indicator_codes`, `conditions`, `quota_calculation`, `result_var_name`, `output_template`) 
VALUES 
('企业信用额度', 'TAX_RATE,PROFIT_RATE', '税负率 < 5%', 'baseAmount * (1 + profitRate * 0.5 - taxRate * 0.3)', 'creditLimit', '{"companyId":"${companyId}","creditLimit":"${creditLimit}","status":"${status}"}');
