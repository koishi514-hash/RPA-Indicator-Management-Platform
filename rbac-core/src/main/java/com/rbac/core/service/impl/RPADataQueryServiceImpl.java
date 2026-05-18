package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.RpaDataQuery;
import com.rbac.core.domain.entity.Task;
import com.rbac.core.domain.mapper.RpaDataQueryMapper;
import com.rbac.core.domain.mapper.TaskMapper;
import com.rbac.core.service.RPADataQueryService;
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
 * 数据查询PA服务实现类
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RPADataQueryServiceImpl extends ServiceImpl<RpaDataQueryMapper, RpaDataQuery> implements RPADataQueryService {

    private final RpaDataQueryMapper rpaDataQueryMapper;
    private final TaskMapper taskMapper;

    /**
     * 分页查询RPA数据查询
     * @param keyword 查询关键词
     * @param taskId 任务ID
     * @param taxAreaId 税区ID
     * @param status 状态
     * @param createTimeStart 创建时间开始
     * @param createTimeEnd 创建时间结束
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public Result<?> pageRPADataQueryList(String keyword, Integer taskId, String taxAreaId, Integer status, String createTimeStart, String createTimeEnd, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<RpaDataQuery> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            // 模糊查询纳税人识别号或企业名称
            queryWrapper.like(RpaDataQuery::getTaxNo, keyword)
                    .or().like(RpaDataQuery::getEnterpriseName, keyword);
        }
        if (taskId != null) {
            queryWrapper.like(RpaDataQuery::getTaskId, taskId);
        }
        if (taxAreaId != null && !taxAreaId.isEmpty()) {
            queryWrapper.like(RpaDataQuery::getCategoryId, taxAreaId);
        }
        if (status != null) {
            queryWrapper.eq(RpaDataQuery::getDataStatus, status);
        }
        if (createTimeStart != null && createTimeEnd != null) {
            try {
                // 将字符串时间转换为LocalDateTime类型，并调整为当天开始和结束时间
                LocalDateTime startDateTime = LocalDateTime.parse(createTimeStart).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime endDateTime = LocalDateTime.parse(createTimeEnd).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                queryWrapper.between(RpaDataQuery::getCreateTime, startDateTime, endDateTime);
            } catch (Exception e) {
                log.error("时间格式转换失败: {}", e.getMessage());
            }
        }
        long total = 0L;
        try {
            total = count(queryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        Page<RpaDataQuery> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            // 发生异常时，返回空分页对象
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        List<RpaDataQuery> items = page.getRecords();
        if (items != null && !items.isEmpty()) {
            for (RpaDataQuery item : items) {
                Map<String, Object> record = new HashMap<>();
                record.put("queryId", item.getId());
                record.put("taskId", item.getTaskId());
                record.put("taxNo", item.getTaxNo());
                record.put("enterpriseName", item.getEnterpriseName());
                record.put("taxAreaId", item.getCategoryId());
                record.put("status", item.getDataStatus());
                record.put("createTime", item.getCreateTime());
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
        data.put("records", records);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /**
     * 查询数据查询详情
     * @param queryId 数据查询ID
     * @return 查询结果
     */
    @Override
    public Result<?> getRPADataQueryDetail(Integer queryId) {
        RpaDataQuery query = rpaDataQueryMapper.selectById(queryId);
        if (query == null) {
            return Result.failed(404, "数据查询不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("queryId", query.getId());
        data.put("taskId", query.getTaskId());
        data.put("taxNo", query.getTaxNo());
        data.put("enterpriseName", query.getEnterpriseName());
        data.put("taxAreaId", query.getCategoryId());
        data.put("status", query.getDataStatus());
        data.put("createTime", query.getCreateTime());
        data.put("businessData", query.getBusinessData());
        return Result.success("success", data);
    }

    /**
     * 删除数据查询
     * @param queryId 数据查询ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRPADataQuery(Integer queryId) {
        try {
            RpaDataQuery query = rpaDataQueryMapper.selectById(queryId);
            if (query == null) {
                return Result.failed(404, "数据查询不存在");
            }

            // 删除采集记录
            boolean deleted = removeById(queryId);
            if (!deleted) {
                return Result.failed(500, "删除数据查询失败");
            } else {
                return Result.success("success");
            }
        } catch (Exception e) {
            log.error("删除数据查询失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除数据查询失败");
        }
    }
}
