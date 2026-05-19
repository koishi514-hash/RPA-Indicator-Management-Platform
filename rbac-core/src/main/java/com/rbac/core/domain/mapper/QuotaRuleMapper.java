package com.rbac.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.core.domain.entity.QuotaRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核规则表 Mapper 接口
 */
@Mapper
public interface QuotaRuleMapper extends BaseMapper<QuotaRule> {
}
