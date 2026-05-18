package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.*;
import com.rbac.core.domain.mapper.*;
import com.rbac.core.service.ExecutionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行记录服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl extends ServiceImpl<ExecutionRecordMapper, ExecutionRecord> implements ExecutionsService {

    private final TaskMapper taskMapper;
//    private final ProcessMapper processMapper;
//    private final RobotMapper robotMapper;
    private final ExecutionStepMapper executionStepMapper;

    private final RpaDataCollectionMapper rpaDataCollectionMapper;
    private final RpaDataParsingMapper rpaDataParsingMapper;
    private final RpaDataProcessingMapper rpaDataProcessingMapper;

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
    @Override
    public Result<?> pageExecutionList(String taskCode, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<ExecutionRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (taskCode != null) {
            queryWrapper.eq(ExecutionRecord::getTaskCode, taskCode);
        }
        if (status != null) {
            queryWrapper.eq(ExecutionRecord::getStatus, status);
        }
        if (startTime != null && endTime != null) {
            try {
                // 处理不同格式的时间字符串
                String start = startTime.replace(" ", "T");
                String end = endTime.replace(" ", "T");
                // 直接使用用户输入的时间范围，不做自动调整
                LocalDateTime startDateTime = LocalDateTime.parse(start);
                LocalDateTime endDateTime = LocalDateTime.parse(end);
                queryWrapper.between(ExecutionRecord::getStartTime, startDateTime, endDateTime);
            } catch (Exception e) {
                log.error("时间格式转换失败: {}", e.getMessage());
            }
        }

        // 分页查询
        Page<ExecutionRecord> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        // 处理执行数据
        List<Map<String, Object>> executionList = new ArrayList<>();
        List<ExecutionRecord> recordList = page.getRecords();
        if (recordList != null && !recordList.isEmpty()) {
            for (ExecutionRecord record : recordList) {
                Map<String, Object> map = new HashMap<>();
                map.put("executionId", record.getExecutionId());
//            // 通过任务编码获取任务ID
//            Long taskId1 = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
//                    .eq(Task::getTaskCode, record.getTaskCode())).getId();
//            map.put("taskId", taskId1);
//            Long processId = processMapper.selectOne(new LambdaQueryWrapper<Process>()
//                    .eq(Process::getProcessCode, record.getProcessCode())).getId();
//            map.put("processId", processId);
//            Long robotId = robotMapper.selectOne(new LambdaQueryWrapper<Robot>()
//                    .eq(Robot::getRobotCode, record.getRobotCode())).getId();
//            map.put("robotId", robotId);
                map.put("taskCode", record.getTaskCode());
                map.put("processCode", record.getProcessCode());
                map.put("robotCode", record.getRobotCode());
                map.put("status", record.getStatus());
                map.put("createTime", record.getCreateTime());
                map.put("updateTime", record.getUpdateTime());
                map.put("duration", record.getDuration());
                map.put("startTime", record.getStartTime());
                map.put("endTime", record.getEndTime());

                // 获取当前执行记录对应的任务Id
                Long currentTaskId = null;
                try {
                    List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                            .eq(Task::getTaskCode, record.getTaskCode()));
                    Task task = tasks.isEmpty() ? null : tasks.get(0);
                    if (task != null) {
                        currentTaskId = task.getId();
                        map.put("taskId", currentTaskId);

                        // 从RPA数据收集表查询错误信息
                        String errorMsg = null;
                        List<RpaDataCollection> collections = rpaDataCollectionMapper.selectList(new LambdaQueryWrapper<RpaDataCollection>()
                                .eq(RpaDataCollection::getTaskId, currentTaskId));
                        RpaDataCollection collection = collections.isEmpty() ? null : collections.get(0);
                        if (collection != null) {
                            errorMsg = collection.getErrorMessage();
                        }
                        // 若RPA数据收集表中没有错误信息，再从RPA数据解析表查询错误信息
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            List<RpaDataParsing> parsings = rpaDataParsingMapper.selectList(new LambdaQueryWrapper<RpaDataParsing>()
                                    .eq(RpaDataParsing::getTaskId, currentTaskId));
                            RpaDataParsing parsing = parsings.isEmpty() ? null : parsings.get(0);
                            if (parsing != null) {
                                errorMsg = parsing.getErrorMessage();
                            }
                        }
                        // 若RPA数据解析表中没有错误信息，再从RPA数据处理表查询错误信息
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            List<RpaDataProcessing> processings = rpaDataProcessingMapper.selectList(new LambdaQueryWrapper<RpaDataProcessing>()
                                    .eq(RpaDataProcessing::getTaskId, currentTaskId));
                            RpaDataProcessing processing = processings.isEmpty() ? null : processings.get(0);
                            if (processing != null) {
                                errorMsg = processing.getErrorMessage();
                            }
                        }
                        map.put("errorMsg", errorMsg);
                    } else {
                        map.put("taskId", null);
                        map.put("errorMsg", null);
                    }
                } catch (Exception e) {
                    log.error("获取任务信息失败: {}", e.getMessage());
                    map.put("taskId", null);
                    map.put("errorMsg", null);
                }
                executionList.add(map);
            }
        }
        // 构建分页结果
        Page<Map<String, Object>> pageResult = new Page<>(pageNum, pageSize);
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(executionList);
        // 构建响应数据
        return Result.success("查询成功", pageResult);
    }

    /**
     * 获取单个执行记录详情
     * @param executionId 执行记录ID
     * @return 执行记录详情
     */
    @Override
    public Result<?> getExecutionDetail(String executionId) {
        List<ExecutionRecord> records = baseMapper.selectList(new LambdaQueryWrapper<ExecutionRecord>()
                .eq(ExecutionRecord::getExecutionId, executionId));
        ExecutionRecord record = records.isEmpty() ? null : records.get(0);
        if (record == null) {
            return Result.failed(404, "执行记录不存在");
        }

        // 构建响应数据
        Map<String, Object> map = new HashMap<>();
        map.put("executionId", record.getExecutionId());
//        // 通过任务编码获取任务ID
//        Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
//                .eq(Task::getTaskCode, record.getTaskCode()));
//        if (task == null) {
//            return Result.failed(404, "任务不存在");
//        }
//        Long taskId = task.getId();
//        map.put("taskId", taskId);
//        Process process = processMapper.selectOne(new LambdaQueryWrapper<Process>()
//                .eq(Process::getProcessCode, record.getProcessCode()));
//        if (process == null) {
//            return Result.failed(404, "流程不存在");
//        }
//        map.put("processId", process.getId());
//        Robot robot = robotMapper.selectOne(new LambdaQueryWrapper<Robot>()
//                .eq(Robot::getRobotCode, record.getRobotCode()));
//        if (robot == null) {
//            return Result.failed(404, "机器人不存在");
//        }
//        map.put("robotId", robot.getId());
        map.put("taskCode", record.getTaskCode());
        map.put("processCode", record.getProcessCode());
        map.put("robotCode", record.getRobotCode());
        map.put("status", record.getStatus());
        map.put("duration", record.getDuration());
        map.put("startTime", record.getStartTime());
        map.put("endTime", record.getEndTime());

        // 从RPA数据收集表查询错误信息
        String errorMsg = null;
        // 获取当前执行记录对应的任务Id
        Long taskId;
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getTaskCode, record.getTaskCode()));
        Task task = tasks.isEmpty() ? null : tasks.get(0);
        if (task != null) {
            taskId = task.getId();
            List<RpaDataCollection> collections = rpaDataCollectionMapper.selectList(new LambdaQueryWrapper<RpaDataCollection>()
                    .eq(RpaDataCollection::getTaskId, taskId));
            RpaDataCollection collection = collections.isEmpty() ? null : collections.get(0);
            if (collection != null) {
                errorMsg = collection.getErrorMessage();
            }
            // 若RPA数据收集表中没有错误信息，再从RPA数据解析表查询错误信息
            if (errorMsg == null || errorMsg.isEmpty()) {
                List<RpaDataParsing> parsings = rpaDataParsingMapper.selectList(new LambdaQueryWrapper<RpaDataParsing>()
                        .eq(RpaDataParsing::getTaskId, taskId));
                RpaDataParsing parsing = parsings.isEmpty() ? null : parsings.get(0);
                if (parsing != null) {
                    errorMsg = parsing.getErrorMessage();
                }
            }
            // 若RPA数据解析表中没有错误信息，再从RPA数据处理表查询错误信息
            if (errorMsg == null || errorMsg.isEmpty()) {
                List<RpaDataProcessing> processings = rpaDataProcessingMapper.selectList(new LambdaQueryWrapper<RpaDataProcessing>()
                        .eq(RpaDataProcessing::getTaskId, taskId));
                RpaDataProcessing processing = processings.isEmpty() ? null : processings.get(0);
                if (processing != null) {
                    errorMsg = processing.getErrorMessage();
                }
            }
        }

        // 从执行步骤中查询错误信息
        List<ExecutionStep> steps = executionStepMapper.selectList(new LambdaQueryWrapper<ExecutionStep>()
                .eq(ExecutionStep::getExecutionId, executionId));
        for (ExecutionStep step : steps) {
            if (step.getOutput() != null && step.getOutput().startsWith("Error: ")) {
                errorMsg = step.getOutput();
                break;
            }
        }

        map.put("errorMsg", errorMsg);

        // 获取执行记录对应的步骤列表
        List<Map<String, Object>> stepLogs = new ArrayList<>();
        List<ExecutionStep> steps1 = executionStepMapper.selectList(new LambdaQueryWrapper<ExecutionStep>()
                .eq(ExecutionStep::getExecutionId, executionId));
        if (steps1 != null && !steps1.isEmpty()) {
            for(ExecutionStep step : steps1) {
                Map<String, Object> stepMap = new HashMap<>();
                stepMap.put("stepName", step.getStepName());
                stepMap.put("stepType", step.getStepType());
                stepMap.put("output", step.getOutput());
                stepMap.put("executeTime", step.getExecuteTime());
                stepLogs.add(stepMap);
            }
        }
        map.put("stepLogs", stepLogs);
        return Result.success("success", map);
    }
}