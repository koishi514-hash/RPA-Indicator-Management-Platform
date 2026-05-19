package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddIndicatorRequest;
import com.rbac.common.model.dto.UpdateIndicatorRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Indicator;

/**
 * 指标服务接口
 */
public interface IndicatorService extends IService<Indicator> {

    /**
     * 分页查询指标列表
     * @param keyword 搜索关键词（指标名称或编码）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 指标列表
     */
    Result<?> pageIndicatorList(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 添加指标
     * @param request 指标添加请求
     * @return 指标ID
     */
    Result<?> addIndicator(AddIndicatorRequest request);

    /**
     * 更新指标
     * @param request 指标更新请求
     * @return 更新结果
     */
    Result<?> updateIndicator(UpdateIndicatorRequest request);

    /**
     * 查询指标详情
     * @param id 指标ID
     * @return 指标详情
     */
    Result<?> getIndicatorDetail(Long id);

    /**
     * 删除指标
     * @param id 指标ID
     * @return 删除结果
     */
    Result<?> deleteIndicator(Long id);

    /**
     * 获取所有指标列表（供审核规则选择使用）
     * @return 指标列表
     */
    Result<?> getAllIndicators();
}
