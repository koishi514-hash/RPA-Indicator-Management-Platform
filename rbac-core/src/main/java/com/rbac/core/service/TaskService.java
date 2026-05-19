package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddTaskRequest;
import com.rbac.common.model.dto.UpdateTaskRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Task;

/**
 * 任务服务接口
 */

public interface TaskService extends IService<Task> {

    /**
     * 分页查询任务列表
     * @param taskCode 任务编码（模糊查询）
     * @param taskName 任务名称（模糊查询）
     * @param status 任务状态（0-待执行，1-执行中，2-成功，3-失败）
     * @param startTime 开始时间（格式：`yyyy-MM-ddTHH:mm:ss`）
     * @param endTime 结束时间（格式：`yyyy-MM-ddTHH:mm:ss`）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 任务列表
     */
    Result<?> pageTaskList(String taskCode, String taskName, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 添加任务
     * @param request 任务添加请求
     * @return 任务ID
     */
    Result<?> addTask(AddTaskRequest request);

    /**
     * 更新任务
     * @param request 任务更新请求
     * @return 更新结果
     */
    Result<?> updateTask(UpdateTaskRequest request);

    /**
     * 查询任务详情
     * @param taskCode 任务编码
     * @return 任务详情
     */
    Result<?> getTaskDetail(String taskCode);

    /**
     * 删除任务
     * @param taskCode 任务编码
     * @return 删除结果
     */
    Result<?> deleteTask(String taskCode);

    /**
     * 执行任务
     * @param taskCode 任务编码
     * @return 执行结果
     */
    Result<?> executeTask(String taskCode);

}
