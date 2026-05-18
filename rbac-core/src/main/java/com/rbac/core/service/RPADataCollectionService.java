package com.rbac.core.service;

import com.rbac.common.model.dto.AddRPADataCollectionRequest;
import com.rbac.common.response.Result;

/**
 * RPA数据采集服务接口
 */

public interface RPADataCollectionService {

    /**
     * 分页查询RPA数据采集列表
     * @param taskCode 任务编码
     * @param keyword 关键词
     * @param status 状态
     * @param collectionTimeStart 采集时间开始
     * @param collectionTimeEnd 采集时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Result<?> pageRPADataCollectionList(String taskCode, String keyword, String status, String collectionTimeStart, String collectionTimeEnd, Integer pageNum, Integer pageSize);

    /**
     * 获取RPA数据采集详情
     * @param collectionId 数据采集ID
     * @return 数据采集详情
     */
    Result<?> getRPADataCollectionDetail(Long collectionId);

    /**
     * 删除RPA数据采集
     * @param collectionId 数据采集ID
     * @return 删除结果
     */
    Result<?> deleteRPADataCollection(Long collectionId);

    /**
     * 添加RPA数据采集
     * @param request 添加RPA数据采集请求
     * @return 添加结果
     */
    Result<?> addRPADataCollection(AddRPADataCollectionRequest request);
}
