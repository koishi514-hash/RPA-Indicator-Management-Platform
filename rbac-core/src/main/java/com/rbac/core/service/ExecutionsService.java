package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.ExecutionRecord;

/**
 * 执行记录服务接口
 */

public interface ExecutionsService extends IService<ExecutionRecord> {

    /**
     * 分页查询执行记录列表
     * @param taskCode 任务编码
     * @param status 执行状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Result<?> pageExecutionList(String taskCode, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 获取单个执行记录详情
     * @param executionId 执行记录ID
     * @return 执行记录详情
     */
    Result<?> getExecutionDetail(String executionId);
}
