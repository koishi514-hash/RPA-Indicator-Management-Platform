package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddProcessRequest;
import com.rbac.common.model.dto.SaveProcessStepRequest;
import com.rbac.common.model.dto.UpdateProcessRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Process;

/**
 * 流程服务接口
 */

public interface ProcessService extends IService<Process> {

    /**
     * 根据流程ID获取流程编码
     * @param id 流程ID
     * @return 流程编码
     */
    String getProcessCodeById(Long id);

    /**
     * 根据流程编码获取流程ID
     * @param processCode 流程编码
     * @return 流程ID
     */
    Long getProcessIdByCode(String processCode);

    /**
     * 分页查询流程列表
     * @param processName 流程名称
     * @param processCode 流程编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 流程列表
     */
    Result<?> pageProcessList(String processName, String processCode, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 添加流程
     * @param request 添加流程请求
     * @return 添加结果
     */
    Result<?> addProcess(AddProcessRequest request);

    /**
     * 更新流程
     * @param request 更新流程请求
     * @return 更新结果
     */
    Result<?> updateProcess(UpdateProcessRequest request);

    /**
     * 根据流程编码获取流程详情
     * @param processCode 流程编码
     * @return 流程详情
     */
    Result<?> getProcessDetails(String processCode);

    /**
     * 根据流程编码获取流程步骤
     * @param processCode 流程编码
     * @return 流程步骤
     */
    Result<?> getProcessStep(String processCode);

    /**
     * 保存流程步骤
     * @param request 保存流程步骤请求
     * @return 保存结果
     */
    Result<?> saveProcessStep(SaveProcessStepRequest request);

    /**
     * 删除流程
     * @param processCode 流程编码
     * @return 删除结果
     */
    Result<?> deleteProcess(String processCode);

    /**
     * 根据流程编码获取流程步骤列表
     * @param processCode 流程编码
     * @return 流程步骤列表
     */
    Result<?> getProcessStepList(String processCode);
}
