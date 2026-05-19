package com.rbac.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.core.domain.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
