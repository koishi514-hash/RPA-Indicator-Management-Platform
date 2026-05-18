package com.rbac.core.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.common.model.dto.AddTaskRequest;
import com.rbac.common.model.dto.UpdateTaskRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.ExecutionRecord;
import com.rbac.core.domain.entity.ExecutionStep;
import com.rbac.core.domain.entity.ProcessStep;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.ExecutionRecordMapper;
import com.rbac.core.domain.mapper.ExecutionStepMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import com.rbac.core.service.ProcessService;
import com.rbac.core.service.ProcessStepExecutor;
import com.rbac.core.service.RobotService;
import com.rbac.core.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final ProcessService processService;
    private final RobotService robotService;
    private final TaskMapper taskMapper;
    private final ExecutionRecordMapper executionMapper;
    private final ProcessStepExecutor processStepExecutor;
    private final ExecutionStepMapper executionStepMapper;
    private final ExecutionRecordMapper executionRecordMapper;

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
    @Override
    public Result<?> pageTaskList(String taskCode, String taskName, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        if (taskCode != null && !taskCode.isEmpty()) {
            queryWrapper.like(Task::getTaskCode, taskCode);
        }
        if (taskName != null && !taskName.isEmpty()) {
            queryWrapper.like(Task::getTaskName, taskName);
        }
        if (status != null) {
            queryWrapper.eq(Task::getStatus, status);
        }
        if (startTime != null && endTime != null) {
            try {
                // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                LocalDateTime startDateTime = LocalDateTime.parse(startTime).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime endDateTime = LocalDateTime.parse(endTime).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                queryWrapper.between(Task::getCreateTime, startDateTime, endDateTime);
            } catch (Exception e) {
                log.error("时间格式转换失败: {}", e.getMessage());
            }
        }

        long total = 0L;
        try{
            total = count(queryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        // 分页查询
        Page<Task> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        // 处理任务数据
        List<Map<String, Object>> records = new ArrayList<>();
        List<Task> taskList = page.getRecords();
        if (taskList != null && !taskList.isEmpty()) {
            for (Task task : taskList) {
                Map<String, Object> record = new HashMap<>();
                record.put("taskId", task.getId());
                record.put("taskCode", task.getTaskCode());
                record.put("taskName", task.getTaskName());
                record.put("taxNo", task.getTaxNo());
                record.put("enterpriseName", task.getEnterpriseName());
                record.put("status", task.getStatus());
                record.put("createTime", task.getCreateTime());
                records.add(record);
            }
        }
        // 构建分页结果
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("total", total);
        pageResult.put("records", records);
        pageResult.put("pageNum", pageNum);
        pageResult.put("pageSize", pageSize);
        // 构建响应数据
        return Result.success("success", pageResult);
    }

    /**
     * 新增任务
     * @param request 新增任务请求参数
     * @return 新增任务结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addTask(AddTaskRequest request) {
        // 检查任务名是否已存在
        Task existingTask = getOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getTaskName, request.getTaskName()));
        if (existingTask != null) {
            return Result.failed("任务名称已存在");
        }

        try {
            // 生成唯一的任务编码
            String taskCode = "TASK+" + System.currentTimeMillis();

            String processCode = processService.getProcessCodeById(request.getProcessId());
            String robotCode = robotService.getRobotCodeById(request.getRobotId());

            // 创建新任务
            Task newTask = new Task();
            newTask.setTaskCode(taskCode);
            newTask.setTaskName(request.getTaskName());
            newTask.setTaxNo(request.getTaxNo());
            newTask.setEnterpriseName(request.getEnterpriseName());
            newTask.setPriority(request.getPriority());
            newTask.setProcessCode(processCode);
            newTask.setRobotCode(robotCode);
            newTask.setPriority(request.getPriority());
            newTask.setDescription(request.getRemark());
            // 任务状态默认待执行
            newTask.setStatus(0);
            newTask.setCreateTime(LocalDateTime.now());

            // 保存任务
            boolean saved = save(newTask);
            if (!saved) {
                return Result.failed("任务新增失败");
            }

            // 返回响应
            Map<String, Object> data = new HashMap<>();
            data.put("taskCode", taskCode);
            return Result.success("success", data);
        } catch (Exception e) {
            log.error("新增任务失败", e);
            return Result.failed("新增任务失败");
        }
    }

    /**
     * 更新任务
     * @param request 任务更新请求
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateTask(UpdateTaskRequest request) {
        try {
            Task task = taskMapper.selectById(request.getId());
            if (task == null) {
                return Result.failed(404, "任务不存在");
            }

            // 更新任务信息
            if (request.getTaskName() != null) {
                task.setTaskName(request.getTaskName());
            }
            if (request.getProcessId() != null) {
                task.setProcessCode(processService.getProcessCodeById(request.getProcessId()));
            }
            if (request.getRobotId() != null) {
                task.setRobotCode(robotService.getRobotCodeById(request.getRobotId()));
            }
            if (request.getTaxNo() != null) {
                task.setTaxNo(request.getTaxNo());
            }
            if (request.getEnterpriseName() != null) {
                task.setEnterpriseName(request.getEnterpriseName());
            }
            if (request.getPriority() != null) {
                task.setPriority(request.getPriority());
            }
            if (request.getRemark() != null) {
                task.setDescription(request.getRemark());
            }
            task.setUpdateTime(LocalDateTime.now());
            // 保存更新
            boolean updated = updateById(task);
            if (updated) {
                return Result.success("success");
            } else {
                return Result.failed(500, "任务更新失败");
            }
        } catch (Exception e) {
            log.error("更新任务失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "更新任务失败");
        }
    }

    /**
     * 获取任务详情
     * @param taskCode 任务编码
     * @return 任务详情
     */
    @Override
    public Result<?> getTaskDetail(String taskCode) {
        Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
                .eq(Task::getTaskCode, taskCode));
        if (task == null) {
            return Result.failed(404, "任务不存在");
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("taskId", task.getId());
        responseData.put("taskCode", task.getTaskCode());
        responseData.put("taskName", task.getTaskName());
        responseData.put("taxNo", task.getTaxNo());
        responseData.put("enterpriseName", task.getEnterpriseName());
        responseData.put("processId", processService.getProcessIdByCode(task.getProcessCode()));
        responseData.put("processCode", task.getProcessCode());
        responseData.put("robotId", robotService.getRobotIdByCode(task.getRobotCode()));
        responseData.put("robotCode", task.getRobotCode());
        responseData.put("status", task.getStatus());
        responseData.put("createTime", task.getCreateTime());
        responseData.put("startTime", task.getStartTime());
        responseData.put("endTime", task.getEndTime());
        responseData.put("remark", task.getDescription());

        return Result.success("success", responseData);
    }

    /**
     * 删除任务
     * @param taskCode 任务编码
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteTask(String taskCode) {
        try {
            // 检查任务是否存在
            Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
                    .eq(Task::getTaskCode, taskCode));
            if (task == null) {
                return Result.failed(404, "任务不存在");
            }

            // 删除任务
            boolean deleted = removeById(task.getId());
            if (!deleted) {
                return Result.failed(500, "任务删除失败");
            }
            return Result.success("success");
        } catch (Exception e) {
            log.error("删除任务失败: {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "任务删除失败");
        }
    }

    /**
     * 执行任务
     * @param taskCode 任务编码
     * @return 执行结果
     */
    @Override
    public Result<?> executeTask(String taskCode) {
        // 根据任务编码查询任务
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getTaskCode, taskCode);
        Task task = getOne(queryWrapper);

        if (task == null) {
            return Result.failed(404, "任务不存在");
        }

        try {
            // 生成唯一的执行ID
            Long executionId = System.currentTimeMillis();

            // 更新任务状态为执行中
            task.setStatus(1);
            task.setStartTime(LocalDateTime.now());
            updateById(task);

            // 更新机器人当前任务ID
            robotService.updateCurrentTaskId(task.getRobotCode(), String.valueOf(task.getId()));

            // 创建执行记录
            ExecutionRecord executionRecord = new ExecutionRecord();
            executionRecord.setExecutionId(String.valueOf(executionId));
            executionRecord.setTaskCode(taskCode);
            executionRecord.setProcessCode(task.getProcessCode());
            executionRecord.setRobotCode(task.getRobotCode());
            executionRecord.setStatus(1);    // 将状态设置为执行中
            executionRecord.setStartTime(LocalDateTime.now());
            executionRecord.setEndTime(LocalDateTime.now());    // 任务完成时更新
            executionRecordMapper.insert(executionRecord);

            // 获取流程步骤
            Result<?> processStepResult = processService.getProcessStep(task.getProcessCode());
            if (!processStepResult.isSuccess()) {
                // 更新执行记录为失败
                executionRecord.setStatus(0);
                executionRecord.setEndTime(LocalDateTime.now());
                executionRecordMapper.updateById(executionRecord);

                // 更新任务状态为失败
                task.setStatus(3);
                task.setEndTime(LocalDateTime.now());
                executionRecordMapper.updateById(executionRecord);

                return Result.failed(500, "获取流程步骤失败");
            }

            @SuppressWarnings("unchecked")
            List<ProcessStep> processSteps = (List<ProcessStep>) processStepResult.getData();

            if (processSteps == null || processSteps.isEmpty()) {
                // 更新执行记录为失败
                executionRecord.setStatus(0);
                executionRecord.setEndTime(LocalDateTime.now());
                updateById(task);

                // 更新任务状态为失败
                task.setStatus(3);
                task.setEndTime(LocalDateTime.now());
                updateById(task);

                return Result.failed(500, "流程步骤为空");
            }

            // 构建执行上下文
            Map<String, Object> context = new HashMap<>();
            context.put("taskId", task.getId());
            context.put("taskCode", taskCode);
            context.put("taskName", task.getTaskName());
            context.put("taxNo", task.getTaxNo());
            context.put("enterpriseName", task.getEnterpriseName());
            context.put("processCode", task.getProcessCode());
            context.put("robotCode", task.getRobotCode());
            context.put("executionId", executionId);

            boolean allStepsSuccess = true;
            String errorMsg = null;

            // 依次执行每个步骤
            for (ProcessStep processStep : processSteps) {
                try {
                    // 执行步骤
                    ExecutionStep executionStep = processStepExecutor.executeStep(processStep, context);
                    // 设置执行ID
                    executionStep.setExecutionId(String.valueOf(executionId));
                    // 保存执行结果
                    executionStepMapper.insert(executionStep);
                    // 如果步骤执行失败, 记录错误信息
                    if (executionStep.getOutput() != null && executionStep.getOutput().startsWith("Error: ")) {
                        allStepsSuccess = false;
                        errorMsg = executionStep.getOutput();
                        break;
                    }

                    // 将当前步骤的输出添加到上下文, 供下一步使用
                    if (executionStep.getOutput() != null) {
                        context.put("stepOutput_"+ processStep.getStepOrder(), executionStep.getOutput());
                        
                        // 尝试解析输出为 JSON，并将顶层 key 合并到上下文
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            Map<String, Object> outputMap = objectMapper.readValue(executionStep.getOutput(), new TypeReference<Map<String, Object>>() {});
                            if (outputMap != null) {
                                for (Map.Entry<String, Object> entry : outputMap.entrySet()) {
                                    context.put(entry.getKey(), entry.getValue());
                                    log.debug("将步骤输出 {}: {} 合并到上下文", entry.getKey(), entry.getValue());
                                }
                            }
                        } catch (Exception e) {
                            log.debug("步骤输出不是 JSON 格式，跳过解析: {}", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("执行步骤失败: {}", processStep.getStepName(), e);
                    allStepsSuccess = false;
                    errorMsg = "执行步骤失败: " + e.getMessage();

                    // 保存失败的执行步骤
                    ExecutionStep failedStep = new ExecutionStep();
                    failedStep.setExecutionId(String.valueOf(executionId));
                    failedStep.setStepName(processStep.getStepName());
                    failedStep.setStepType(processStep.getStepType());
                    failedStep.setExecuteTime(LocalDateTime.now());
                    failedStep.setOutput("Error: " + e.getMessage());
                    executionStepMapper.insert(failedStep);

                    break;
                }
            }

            // 更新执行记录
            executionRecord.setEndTime(LocalDateTime.now());
            if (allStepsSuccess) {
                executionRecord.setStatus(1);    // 成功
            } else {
                executionRecord.setStatus(0);    // 失败
            }

            // 计算执行时长
            long duration = Duration.between(executionRecord.getStartTime(),
                    executionRecord.getEndTime()).getSeconds();
            if (duration < 60) {
                executionRecord.setDuration(duration + "秒");
            } else {
                long minutes = duration / 60;
                long seconds = duration % 60;
                executionRecord.setDuration(minutes + "分" + seconds + "秒");
            }

            executionRecordMapper.updateById(executionRecord);

            // 更新任务状态
            task.setEndTime(LocalDateTime.now());
            if (allStepsSuccess) {
                task.setStatus(2);    // 已完成
            } else {
                task.setStatus(3);    // 失败
            }
            updateById(task);

            // 任务执行完成，清空机器人当前任务ID
            robotService.updateCurrentTaskId(task.getRobotCode(), null);
            // 更新机器人最后心跳时间
            robotService.updateLastHeartbeat(task.getRobotCode());

            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("executionId", executionId);
            data.put("status", allStepsSuccess ? "success" : "failed");
            if (!allStepsSuccess) {
                data.put("errorMsg", errorMsg);
            }
            return Result.success("success", data);
        } catch (Exception e) {
            log.error("执行任务失败: {}", e.getMessage(), e);
            // 发生异常时，也要清空机器人当前任务ID
            if (task != null) {
                robotService.updateCurrentTaskId(task.getRobotCode(), null);
                // 更新机器人最后心跳时间
                robotService.updateLastHeartbeat(task.getRobotCode());
            }
            return Result.failed(500, "执行任务失败: " + e.getMessage());
        }
    }
}
