package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.RpaDataParsing;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.RpaDataParsingMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPA数据解析服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RPADataParsingServiceImpl extends ServiceImpl<RpaDataParsingMapper, RpaDataParsing> implements com.rbac.core.service.RPADataParsingService {

    private final RpaDataParsingMapper rpaDataParsingMapper;
    private final TaskMapper taskMapper;

    @Override
    public Result<?> pageRPADataParsingList(Integer taskId, String status, String parsingTimeStart, String parsingTimeEnd, Integer pageNum, Integer pageSize) {
        // 构建基础查询条件
        LambdaQueryWrapper<RpaDataParsing> baseQueryWrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            baseQueryWrapper.eq(RpaDataParsing::getTaskId, taskId);
        }
        if (status != null) {
            baseQueryWrapper.eq(RpaDataParsing::getStatus, status);
        }
        if (parsingTimeStart != null && parsingTimeEnd != null) {
            baseQueryWrapper.between(RpaDataParsing::getParsingTime, parsingTimeStart, parsingTimeEnd);
        }

        Long total = count(baseQueryWrapper);
        Long totalParsing = total;
        
        // 为每个状态创建单独的查询条件
        LambdaQueryWrapper<RpaDataParsing> successQueryWrapper = new LambdaQueryWrapper<>();
        successQueryWrapper.eq(RpaDataParsing::getStatus, "processed");
        if (taskId != null) {
            successQueryWrapper.eq(RpaDataParsing::getTaskId, taskId);
        }
        if (parsingTimeStart != null && parsingTimeEnd != null) {
            successQueryWrapper.between(RpaDataParsing::getParsingTime, parsingTimeStart, parsingTimeEnd);
        }
        Long success = count(successQueryWrapper);
        
        LambdaQueryWrapper<RpaDataParsing> parsingQueryWrapper = new LambdaQueryWrapper<>();
        parsingQueryWrapper.eq(RpaDataParsing::getStatus, "processing");
        if (taskId != null) {
            parsingQueryWrapper.eq(RpaDataParsing::getTaskId, taskId);
        }
        if (parsingTimeStart != null && parsingTimeEnd != null) {
            parsingQueryWrapper.between(RpaDataParsing::getParsingTime, parsingTimeStart, parsingTimeEnd);
        }
        Long parsing = count(parsingQueryWrapper);
        
        LambdaQueryWrapper<RpaDataParsing> failedQueryWrapper = new LambdaQueryWrapper<>();
        failedQueryWrapper.eq(RpaDataParsing::getStatus, "failed");
        if (taskId != null) {
            failedQueryWrapper.eq(RpaDataParsing::getTaskId, taskId);
        }
        if (parsingTimeStart != null && parsingTimeEnd != null) {
            failedQueryWrapper.between(RpaDataParsing::getParsingTime, parsingTimeStart, parsingTimeEnd);
        }
        Long failed = count(failedQueryWrapper);

        // 分页查询
        Page<RpaDataParsing> page = new Page<>(pageNum, pageSize);
        page = page(page, baseQueryWrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        List<RpaDataParsing> itemList = page.getRecords();
        if (itemList == null || itemList.isEmpty()) {
            return Result.success("无该数据");
        }
        for (RpaDataParsing item : itemList){
            Map<String, Object> record = new HashMap<>();
            record.put("parsingId", item.getId());
            record.put("taskId", item.getTaskId());
            record.put("collectionId", item.getCollectionId());
            record.put("status", item.getStatus());
            record.put("extractedFields", 0);
            record.put("parsingRule", null);
            record.put("parsingTime", item.getParsingTime());
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

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("statistics", Map.of(
                "totalParsing", totalParsing,
                "success", success,
                "parsing", parsing,
                "failed", failed
        ));
        data.put("records", records);
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
//        System.out.println(data);
        return Result.success(data);
    }

    /**
     * 查询RPA数据解析详情
     * @param parsingId 解析ID
     * @return 数据解析详情
     */
    @Override
    public Result<?> getRPADataParsingDetail(Integer parsingId) {
        if (parsingId == null) {
            return Result.failed(400, "解析ID不能为空");
        }
        RpaDataParsing parsing = rpaDataParsingMapper.selectById(parsingId);
        if (parsing == null) {
            return Result.failed(404, "数据解析不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("parsingId", parsing.getId());
        data.put("taskId", parsing.getTaskId());
        data.put("collectionId", parsing.getCollectionId());
        data.put("taxNo", parsing.getTaxNo());
        data.put("enterpriseName", parsing.getEnterpriseName());
        data.put("status", parsing.getStatus());
        data.put("extractedFields", 0);
        data.put("parsingRule", null);
        data.put("parsingTime", parsing.getParsingTime());
        data.put("parsedData", parsing.getParsedData());
        data.put("errorMsg", parsing.getErrorMessage());
        return Result.success("success", data);
    }

    /**
     * 删除RPA数据解析
     * @param parsingId 解析ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRPADataParsing(Integer parsingId) {
        try {
            RpaDataParsing parsing = rpaDataParsingMapper.selectById(parsingId);
            if (parsing == null) {
                return Result.failed(404, "数据解析不存在");
            }

            // 删除解析记录
            boolean deleted = removeById(parsingId);
            if (!deleted) {
                return Result.failed(500, "删除数据解析失败");
            } else {
                return Result.success("success");
            }
        } catch (Exception e) {
            log.error("删除数据解析失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除数据解析失败");
        }
    }
}
