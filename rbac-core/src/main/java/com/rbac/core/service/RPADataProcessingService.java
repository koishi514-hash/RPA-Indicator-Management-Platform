package com.rbac.core.service;

import com.rbac.common.response.Result;

/**
 * RPA数据处理服务接口
 */

public interface RPADataProcessingService {

    /**
     * 分页查询RPA数据处理列表
     * @param taskId 任务ID
     * @param status 状态
     * @param processingTimeStart 处理时间开始
     * @param processingTimeEnd 处理时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    Result<?> pageRPADataProcessingList(Integer taskId, String status, String processingTimeStart, String processingTimeEnd, Integer pageNum, Integer pageSize);

    /**
     * 获取RPA数据处理详情
     * @param processingId 处理ID
     * @return 处理详情
     */
    Result<?> getRPADataProcessingDetail(Integer processingId);

    /**
     * 删除RPA数据处理
     * @param processingId 处理ID
     * @return 删除结果
     */
    Result<?> deleteRPADataProcessing(Integer processingId);
}
