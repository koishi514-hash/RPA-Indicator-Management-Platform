package com.rbac.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.core.domain.entity.Indicator;
import org.apache.ibatis.annotations.Mapper;

/**
 * 指标表Mapper接口
 */
@Mapper
public interface IndicatorMapper extends BaseMapper<Indicator> {
}
