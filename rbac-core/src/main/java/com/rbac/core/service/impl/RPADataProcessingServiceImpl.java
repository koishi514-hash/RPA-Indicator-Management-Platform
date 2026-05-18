package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.RpaDataProcessing;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.RpaDataProcessingMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import com.rbac.core.service.RPADataProcessingService;
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
 * RPA数据处理服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RPADataProcessingServiceImpl extends ServiceImpl<RpaDataProcessingMapper, RpaDataProcessing> implements RPADataProcessingService {

    private final RpaDataProcessingMapper rpaDataProcessingMapper;
    private final TaskMapper taskMapper;

    /**
     * 分页查询RPA数据加工列表
     * @param taskId 任务ID
     * @param status 状态
     * @param processingTimeStart 开始时间
     * @param processingTimeEnd 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    @Override
    public Result<?> pageRPADataProcessingList(
            Integer taskId, String status, String processingTimeStart,
            String processingTimeEnd, Integer pageNum, Integer pageSize) {
        // 构建基础查询条件
        LambdaQueryWrapper<RpaDataProcessing> baseQueryWrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            baseQueryWrapper.eq(RpaDataProcessing::getTaskId, taskId);
        }
        if (status != null) {
            baseQueryWrapper.eq(RpaDataProcessing::getStatus, status);
        }
        if (processingTimeStart != null && processingTimeEnd != null) {
            try {
                // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                LocalDateTime startDateTime = LocalDateTime.parse(processingTimeStart).withHour(0).withMinute(0)
                        .withSecond(0).withNano(0);
                LocalDateTime endDateTime = LocalDateTime.parse(processingTimeEnd).withHour(23).withMinute(59)
                        .withSecond(59).withNano(999999999);
                baseQueryWrapper.between(RpaDataProcessing::getProcessingTime, startDateTime, endDateTime);
            } catch (Exception e) {
                log.error("时间格式转换失败: {}", e.getMessage());
            }
        }

        long total = 0L;
        long totalProcessing = 0L;
        long success = 0L;
        long pending = 0L;
        long failed = 0L;
        
        try {
            total = count(baseQueryWrapper);
            totalProcessing = total;
            
            // 为每个状态创建单独的查询条件
            LambdaQueryWrapper<RpaDataProcessing> successQueryWrapper = new LambdaQueryWrapper<>();
            successQueryWrapper.eq(RpaDataProcessing::getStatus, "exported");
            if (taskId != null) {
                successQueryWrapper.eq(RpaDataProcessing::getTaskId, taskId);
            }
            if (processingTimeStart != null && processingTimeEnd != null) {
                try {
                    // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                    LocalDateTime startDateTime = LocalDateTime.parse(processingTimeStart).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime endDateTime = LocalDateTime.parse(processingTimeEnd).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                    successQueryWrapper.between(RpaDataProcessing::getProcessingTime, startDateTime, endDateTime);
                } catch (Exception e) {
                    log.error("时间格式转换失败: {}", e.getMessage());
                }
            }
            success = count(successQueryWrapper);
            
            LambdaQueryWrapper<RpaDataProcessing> pendingQueryWrapper = new LambdaQueryWrapper<>();
            pendingQueryWrapper.eq(RpaDataProcessing::getStatus, "processed");
            if (taskId != null) {
                pendingQueryWrapper.eq(RpaDataProcessing::getTaskId, taskId);
            }
            if (processingTimeStart != null && processingTimeEnd != null) {
                try {
                    // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                    LocalDateTime startDateTime = LocalDateTime.parse(processingTimeStart).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime endDateTime = LocalDateTime.parse(processingTimeEnd).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                    pendingQueryWrapper.between(RpaDataProcessing::getProcessingTime, startDateTime, endDateTime);
                } catch (Exception e) {
                    log.error("时间格式转换失败: {}", e.getMessage());
                }
            }
            pending = count(pendingQueryWrapper);
            
            LambdaQueryWrapper<RpaDataProcessing> failedQueryWrapper = new LambdaQueryWrapper<>();
            failedQueryWrapper.eq(RpaDataProcessing::getStatus, "failed");
            if (taskId != null) {
                failedQueryWrapper.eq(RpaDataProcessing::getTaskId, taskId);
            }
            if (processingTimeStart != null && processingTimeEnd != null) {
                try {
                    // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                    LocalDateTime startDateTime = LocalDateTime.parse(processingTimeStart).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime endDateTime = LocalDateTime.parse(processingTimeEnd).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                    failedQueryWrapper.between(RpaDataProcessing::getProcessingTime, startDateTime, endDateTime);
                } catch (Exception e) {
                    log.error("时间格式转换失败: {}", e.getMessage());
                }
            }
            failed = count(failedQueryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        // 分页查询
        Page<RpaDataProcessing> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, baseQueryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        List<RpaDataProcessing> items = page.getRecords();
        if (items != null && !items.isEmpty()) {
            for (RpaDataProcessing item : items) {
                Map<String, Object> record = new HashMap<>();
                record.put("processingId", item.getId());
                record.put("taskId", item.getTaskId());
                record.put("parsingId", item.getParsingId());
                record.put("status", item.getStatus());
                record.put("processingTime", item.getProcessingTime());
                record.put("errorMsg", item.getErrorMessage());
                String taskCode = null;
                try {
                    Task task = taskMapper.selectById(item.getTaskId());
                    if (task != null) {
                        taskCode = task.getTaskCode();
                    }
                } catch (Exception e) {
                    log.error("根据任务ID查询任务编码失败", e);
                }
                record.put("taskCode", taskCode);
                records.add(record);
            }
        }

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("statistics", Map.of(
                "totalProcessing", totalProcessing,
                "success", success,
                "pending", pending,
                "failed", failed
        ));
        data.put("records", records);
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.success("success", data);
    }

    /**
     * 获取RPA数据加工详情
     * @param processingId 数据加工ID
     * @return 数据加工详情
     */
    @Override
    public Result<?> getRPADataProcessingDetail(Integer processingId) {
        RpaDataProcessing processing = rpaDataProcessingMapper.selectById(processingId);
        if (processing == null) {
            return Result.failed(404, "数据加工不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("processingId", processing.getId());
        data.put("taskId", processing.getTaskId());
        data.put("parsingId", processing.getParsingId());
        data.put("taxNo", processing.getTaxNo());
        data.put("enterpriseName", processing.getEnterpriseName());
        data.put("status", processing.getStatus());
        data.put("processingTime", processing.getProcessingTime());
        data.put("errorMsg", processing.getErrorMessage());
        data.put("processedData", processing.getProcessedData());
        data.put("verifyResult", processing.getValidationResult());
        return Result.success("success", data);
    }

    /**
     * 删除RPA数据加工
     * @param processingId 数据加工ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRPADataProcessing(Integer processingId) {
        try {
            RpaDataProcessing processing = rpaDataProcessingMapper.selectById(processingId);
            if (processing == null) {
                return Result.failed(404, "数据加工不存在");
            }

            // 删除加工记录
            boolean deleted = removeById(processingId);
            if (!deleted) {
                return Result.failed(500, "删除数据加工失败");
            } else {
                return Result.success("success");
            }
        } catch (Exception e) {
            log.error("删除RPA数据加工失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除数据加工失败");
        }
    }
}
