package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddProcessRequest;
import com.rbac.common.model.dto.ProcessStepDTO;
import com.rbac.common.model.dto.SaveProcessStepRequest;
import com.rbac.common.model.dto.UpdateProcessRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.ExecutionRecord;
import com.rbac.core.domain.entity.Process;
import com.rbac.core.domain.entity.ProcessStep;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.ExecutionRecordMapper;
import com.rbac.core.domain.mapper.ProcessMapper;
import com.rbac.core.domain.mapper.ProcessStepMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import com.rbac.core.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    private final ProcessStepMapper processStepMapper;
    private final TaskMapper taskMapper;
    private final ExecutionRecordMapper executionRecordMapper;

    /**
     * 根据流程ID获取流程编码
     * @param id 流程ID
     * @return 流程编码
     */
    @Override
    public String getProcessCodeById(Long id) {
        Process process = baseMapper.selectById(id);
        return process.getProcessCode();
    }

    /**
     * 根据流程编码获取流程ID
     * @param processCode 流程编码
     * @return 流程ID
     */
    @Override
    public Long getProcessIdByCode(String processCode) {
        Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessCode, processCode));
        return process.getId();
    }

    /**
     * 分页查询流程列表
     * @param processName 流程名称
     * @param processCode 流程编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 流程列表
     */
    @Override
    public Result<?> pageProcessList(String processName, String processCode, Integer status, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Process> queryWrapper = new LambdaQueryWrapper<>();
        if (processName != null) {
            queryWrapper.like(Process::getProcessName, processName);
        }
        if (processCode != null) {
            queryWrapper.like(Process::getProcessCode, processCode);
        }
        if (status != null) {
            queryWrapper.eq(Process::getStatus, status);
        }

        long total = 0L;
        try{
            total = count(queryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        // 分页查询
        Page<Process> page = new Page<>(pageNum, pageSize);
        try {
            baseMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        // 返回分页结果
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("total", total);
        pageResult.put("records", page.getRecords());
        pageResult.put("pageNum", pageNum);
        pageResult.put("pageSize", pageSize);
        return Result.success(pageResult);
    }

    /**
     * 新增流程
     * @param request 新增流程请求
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addProcess(AddProcessRequest request) {
        // 检查用户名是否存在
        Process existingProcess = baseMapper.selectOne(new LambdaQueryWrapper<Process>()
                .eq(Process::getProcessCode, request.getProcessCode()));
        if (existingProcess != null) {
            return Result.failed(400, "流程编码已存在");
        }

        // 创建新流程
        Process process = new Process();
        process.setProcessCode(request.getProcessCode());
        process.setProcessName(request.getProcessName());
        process.setDescription(request.getDescription());
        process.setCreateTime(LocalDateTime.now());
        process.setUpdateTime(LocalDateTime.now());
        // 默认启用
        process.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        try{
            boolean saved = save(process);
            if (!saved) {
                return Result.failed(500, "新增流程失败");
            }
            return Result.success("流程新增成功");
        } catch (Exception e) {
            log.error("新增流程失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "新增流程失败");
        }
    }

    /**
     * 更新流程
     * @param request 更新流程请求
     * @return 更新结果
     */
    @Override
    @Transactional
    public Result<?> updateProcess(UpdateProcessRequest request) {
        Process process = baseMapper.selectById(request.getProcessId());
        if (process == null) {
            return Result.failed(400, "流程不存在");
        }
        process.setProcessCode(request.getProcessCode());
        process.setProcessName(request.getProcessName());
        process.setDescription(request.getDescription());
        process.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        process.setUpdateTime(LocalDateTime.now());
        try{
            boolean saved = updateById(process);
            if (!saved) {
                return Result.failed(500, "更新流程失败");
            }
            return Result.success("流程更新成功");
        } catch (Exception e) {
            log.error("更新流程失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "更新流程失败");
        }
    }

    /**
     * 根据流程编码获取流程详情
     * @param processCode 流程编码
     * @return 流程详情
     */
    @Override
    public Result<?> getProcessDetails(String processCode) {
        Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>()
                .eq(Process::getProcessCode, processCode));
        if (process == null) {
            return Result.failed(400, "流程不存在");
        }

        // 获取流程详情
        return Result.success("success", process);
    }

    /**
     * 根据流程编码获取流程步骤
     * @param processCode 流程编码
     * @return 流程步骤
     */
    @Override
    public Result<?> getProcessStep(String processCode) {
        // 构建查询条件
        LambdaQueryWrapper<ProcessStep> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessStep::getProcessCode, processCode);

        // 按照步骤执行查询排序
        queryWrapper.orderByAsc(ProcessStep::getStepOrder);

        // 查询流程步骤列表
        List<ProcessStep> processSteps = processStepMapper.selectList(queryWrapper);

        return Result.success("success", processSteps);
    }

    /**
     * 保存流程步骤
     * @param request 保存流程步骤请求
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> saveProcessStep(SaveProcessStepRequest request) {
        try {
            String processCode = request.getProcessCode();

            // 检查流程是否存在
            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>()
                    .eq(Process::getProcessCode, processCode));
            if (process == null) {
                return Result.failed(400, "流程不存在");
            }

            // 保存新的流程步骤
        List<ProcessStepDTO> processSteps = request.getSteps();
        
        // 获取当前流程的所有步骤
        LambdaQueryWrapper<ProcessStep> currentStepsQuery = new LambdaQueryWrapper<>();
        currentStepsQuery.eq(ProcessStep::getProcessCode, processCode);
        List<ProcessStep> currentSteps = processStepMapper.selectList(currentStepsQuery);
        
        // 提取新步骤列表中的stepOrder
        List<Integer> newStepOrders = new ArrayList<>();
        if (processSteps != null && !processSteps.isEmpty()) {
            for (ProcessStepDTO step : processSteps) {
                newStepOrders.add(step.getStepOrder());
            }
        }
        
        // 删除不在新列表中的步骤
        for (ProcessStep currentStep : currentSteps) {
            if (!newStepOrders.contains(currentStep.getStepOrder())) {
                processStepMapper.deleteById(currentStep.getId());
            }
        }
        
        // 处理新增和更新步骤
        if (processSteps != null && !processSteps.isEmpty()) {
            for (ProcessStepDTO step : processSteps) {
                // 检查是否存在相同的步骤排序的步骤
                LambdaQueryWrapper<ProcessStep> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ProcessStep::getProcessCode, processCode)
                        .eq(ProcessStep::getStepOrder, step.getStepOrder());
                ProcessStep existingStep = processStepMapper.selectOne(queryWrapper);

                if (existingStep != null) {
                    // 更新现有步骤
                    existingStep.setStepName(step.getStepName());
                    existingStep.setStepType(step.getStepType());
                    existingStep.setCodeContent(step.getCodeContent());
                    existingStep.setDescription(step.getDescription());
                    processStepMapper.updateById(existingStep);
                } else {
                    // 新增步骤
                    ProcessStep newStep = new ProcessStep();
                    newStep.setProcessCode(processCode);
                    newStep.setStepOrder(step.getStepOrder());
                    newStep.setStepName(step.getStepName());
                    newStep.setStepType(step.getStepType());
                    newStep.setCodeContent(step.getCodeContent());
                    newStep.setDescription(step.getDescription());
                    processStepMapper.insert(newStep);
                }
            }
        }

            // 统计流程步骤数并更新到流程表
            LambdaQueryWrapper<ProcessStep> stepCountQuery = new LambdaQueryWrapper<>();
            stepCountQuery.eq(ProcessStep::getProcessCode, processCode);
            int stepCount = Math.toIntExact(processStepMapper.selectCount(stepCountQuery));
            
            // 更新流程的步骤数
            process.setStepCount(stepCount);
            process.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(process);

            return Result.success("流程步骤保存成功");
        } catch (Exception e) {
            log.error("保存流程步骤失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "保存流程步骤失败");
        }
    }

    /**
     * 删除流程
     * @param processCode 流程编码
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteProcess(String processCode) {
        try {
            // 检查流程是否存在
            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>()
                    .eq(Process::getProcessCode, processCode));
            if (process == null) {
                return Result.failed(400, "流程不存在");
            }

            // 检查是否存在相关任务记录
            int taskCount = Math.toIntExact(taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                    .eq(Task::getProcessCode, processCode)));
            if (taskCount > 0) {
                return Result.failed(400, "流程下存在相关任务记录, 不能删除");
            }

            // 检查是否存在相关执行记录
//            int executionCount = Math.toIntExact(executionRecordMapper.selectCount(new LambdaQueryWrapper<ExecutionRecord>()
//                    .eq(ExecutionRecord::getProcessCode, processCode)));
//            if (executionCount > 0) {
//                return Result.failed(400, "流程下存在相关执行记录, 不能删除");
//            }

            // 删除对应的流程步骤
            LambdaQueryWrapper<ProcessStep> stepLambdaQueryWrapper = new LambdaQueryWrapper<ProcessStep>()
                    .eq(ProcessStep::getProcessCode, processCode);
            processStepMapper.delete(stepLambdaQueryWrapper);

            // 删除流程
            boolean deleted = baseMapper.delete(new LambdaQueryWrapper<Process>()
                    .eq(Process::getProcessCode, processCode)) > 0;
            if (deleted) {
                return Result.success("流程删除成功");
            }
            return Result.failed(500, "删除流程失败");
        } catch (Exception e) {
            log.error("删除流程失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除流程失败");
        }
    }

    /**
     * 获取流程步骤列表
     * @param processCode 流程编码
     * @return 流程步骤列表
     */
    @Override
    public Result<?> getProcessStepList(String processCode) {
        LambdaQueryWrapper<ProcessStep> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessStep::getProcessCode, processCode);

        List<Map<String, Object>> records = new ArrayList<>();
        for (ProcessStep step : processStepMapper.selectList(queryWrapper)) {
            Map<String, Object> record = new HashMap<>();
            record.put("stepOrder", step.getStepOrder());
            record.put("stepName", step.getStepName());
            record.put("stepType", step.getStepType());
            record.put("codeContent", step.getCodeContent());
            record.put("description", step.getDescription());
            records.add(record);
        }
        return Result.success(records);
    }
}