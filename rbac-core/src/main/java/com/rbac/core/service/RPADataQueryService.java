package com.rbac.core.service;

import com.rbac.common.response.Result;

/**
 * 数据查询PA服务接口
 */

public interface RPADataQueryService {

    /**
     * 分页查询RPA数据查询列表
     * @param keyword 查询关键词
     * @param taskId 任务ID
     * @param taxAreaId 税区ID
     * @param status 状态
     * @param createTimeStart 创建时间开始
     * @param createTimeEnd 创建时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    Result<?> pageRPADataQueryList(
            String keyword, Integer taskId, String taxAreaId, Integer status, String createTimeStart,
            String createTimeEnd, Integer pageNum, Integer pageSize);

    /**
     * 获取RPA数据查询详情
     * @param queryId 数据查询ID
     * @return 数据查询详情
     */
    Result<?> getRPADataQueryDetail(Integer queryId);

    /**
     * 删除RPA数据查询
     * @param queryId 数据查询ID
     * @return 删除结果
     */
    Result<?> deleteRPADataQuery(Integer queryId);
}
