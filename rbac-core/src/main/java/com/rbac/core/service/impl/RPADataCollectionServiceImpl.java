package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddRPADataCollectionRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.RpaDataCollection;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.RpaDataCollectionMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import com.rbac.core.service.RPADataCollectionService;
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
 * RPA数据采集服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RPADataCollectionServiceImpl extends ServiceImpl<RpaDataCollectionMapper, RpaDataCollection> implements RPADataCollectionService {

    private final RpaDataCollectionMapper rpaDataCollectionMapper;
    private final TaskMapper taskMapper;

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
    @Override
    public Result<?> pageRPADataCollectionList(String taskCode, String keyword, String status, String collectionTimeStart, String collectionTimeEnd, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<RpaDataCollection> queryWrapper = new LambdaQueryWrapper<>();
        if (taskCode != null) {
            try {
                Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>().like(Task::getTaskCode, taskCode));
                if (task != null) {
                    queryWrapper.eq(RpaDataCollection::getTaskId, task.getId());
                }
            } catch (Exception e) {
                log.error("根据任务编码查询任务失败: {}", e.getMessage());
            }
        }
        if (keyword != null && !keyword.isEmpty()) {
            // 模糊查询纳税人识别号或企业名称
            queryWrapper.like(RpaDataCollection::getTaxNo, keyword)
                    .or().like(RpaDataCollection::getEnterpriseName, keyword);
        }
        if (status != null) {
            queryWrapper.eq(RpaDataCollection::getStatus, status);
        }
        if (collectionTimeStart != null && collectionTimeEnd != null) {
            queryWrapper.between(RpaDataCollection::getCollectionTime, collectionTimeStart, collectionTimeEnd);
        }

        Long total = 0L;
        Long totalCollection = 0L;
        Long success = 0L;
        Long pending = 0L;
        Long failed = 0L;

        try {
            total = count(queryWrapper);
            totalCollection = total;
            success = count(new LambdaQueryWrapper<RpaDataCollection>().eq(RpaDataCollection::getStatus, "parsed"));
            pending = count(new LambdaQueryWrapper<RpaDataCollection>().eq(RpaDataCollection::getStatus, "parsing"));
            failed = count(new LambdaQueryWrapper<RpaDataCollection>().eq(RpaDataCollection::getStatus, "failed"));
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        // 分页查询
        Page<RpaDataCollection> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        List<RpaDataCollection> items = page.getRecords();
        if (items != null && !items.isEmpty()) {
            for (RpaDataCollection item : items) {
                Map<String, Object> record = new HashMap<>();
                record.put("collectionId", item.getId());
                record.put("taskId", item.getTaskId());
                record.put("status", item.getStatus());
                record.put("taxNo", item.getTaxNo());
                record.put("enterpriseName", item.getEnterpriseName());
                record.put("dataSource", item.getDataSource());
                record.put("collectionTime", item.getCollectionTime());
                record.put("errorMsg", item.getErrorMessage());
                String taskCodeForRecord = null;
                try {
                    Task task = taskMapper.selectById(item.getTaskId());
                    if (task != null) {
                        taskCodeForRecord = task.getTaskCode();
                    }
                } catch (Exception e) {
                    log.error("根据任务ID查询任务编码失败", e);
                }
                record.put("taskCode", taskCodeForRecord);
                records.add(record);
            }
        }

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("statistics", Map.of(
                "totalCollection", totalCollection,
                "success", success,
                "pending", pending,
                "failed", failed
        ));
        data.put("records", records);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        return Result.success("success", data);
    }

    /**
     * 获取RPA数据采集详情
     * @param collectionId 数据采集ID
     * @return 数据采集详情
     */
    @Override
    public Result<?> getRPADataCollectionDetail(Long collectionId) {
        RpaDataCollection collection = rpaDataCollectionMapper.selectById(collectionId);
        if (collection == null) {
            return Result.failed(404, "数据采集不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("collectionId", collection.getId());
        data.put("taskId", collection.getTaskId());
        data.put("taxNo", collection.getTaxNo());
        data.put("enterpriseName", collection.getEnterpriseName());
        data.put("status", collection.getStatus());
        data.put("dataSource", collection.getDataSource());
        data.put("collectionTime", collection.getCollectionTime());
        data.put("errorMsg", collection.getErrorMessage());
        data.put("rawData", collection.getRawData());
        return Result.success("success", data);
    }

    /**
     * 删除RPA数据采集
     * @param collectionId 数据采集ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRPADataCollection(Long collectionId) {
        try {
            RpaDataCollection collection = rpaDataCollectionMapper.selectById(collectionId);
            if (collection == null) {
                return Result.failed(404, "数据采集不存在");
            }

            // 删除采集记录
            boolean deleted = removeById(collectionId);
            if (!deleted) {
                return Result.failed(500, "删除数据采集失败");
            } else {
                return Result.success("success");
            }
        } catch (Exception e) {
            log.error("删除数据采集失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除数据采集失败");
        }
    }

    /**
     * 添加RPA数据采集
     * @param request 添加请求
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRPADataCollection(AddRPADataCollectionRequest request) {
        try {
            RpaDataCollection collection = new RpaDataCollection();
            collection.setTaskId(request.getTaskId());
            collection.setTaxNo(request.getTaxNo());
            collection.setEnterpriseName(request.getEnterpriseName());
            collection.setStatus("success");
            collection.setDataSource(request.getDataSource());
            collection.setCollectionTime(LocalDateTime.now());
            collection.setErrorMessage(request.getErrorMsg());
            collection.setRawData(request.getRawData());

            // 保存数据
            boolean saved = save(collection);
            if (!saved) {
                return Result.failed(500, "添加数据采集失败");
            }
            return Result.success("success");
        } catch (Exception e) {
            log.error("添加数据采集失败: {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "添加数据采集失败");
        }
    }
}