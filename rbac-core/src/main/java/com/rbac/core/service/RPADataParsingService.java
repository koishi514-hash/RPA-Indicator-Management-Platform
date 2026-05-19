package com.rbac.core.service;

import com.rbac.common.response.Result;

/**
 * RPA数据解析服务接口
 */

public interface RPADataParsingService {

    /**
     * 分页查询RPA数据解析列表
     * @param taskId 任务ID
     * @param status 状态
     * @param parsingTimeStart 解析时间开始
     * @param parsingTimeEnd 解析时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Result<?> pageRPADataParsingList(Integer taskId, String status, String parsingTimeStart, String parsingTimeEnd, Integer pageNum, Integer pageSize);

    /**
     * 查询RPA数据解析详情
     * @param parsingId 解析ID
     * @return 数据解析详情
     */
    Result<?> getRPADataParsingDetail(Integer parsingId);

    /**
     * 删除RPA数据解析
     * @param parsingId 解析ID
     * @return 删除结果
     */
    Result<?> deleteRPADataParsing(Integer parsingId);
}
