package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddIndicatorRequest;
import com.rbac.common.model.dto.UpdateIndicatorRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Indicator;
import com.rbac.core.domain.entity.QuotaRule;
import com.rbac.core.domain.mapper.IndicatorMapper;
import com.rbac.core.domain.mapper.QuotaRuleMapper;
import com.rbac.core.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl extends ServiceImpl<IndicatorMapper, Indicator> implements IndicatorService {

    private final IndicatorMapper indicatorMapper;
    private final QuotaRuleMapper quotaRuleMapper;

    /**
     * 分页查询指标列表
     * @param keyword 搜索关键词（指标名称或编码）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 指标列表
     */
    @Override
    public Result<?> pageIndicatorList(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Indicator> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(Indicator::getIndicatorName, keyword)
                    .or()
                    .like(Indicator::getIndicatorCode, keyword);
        }

        long total = 0L;
        try {
            total = count(queryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        Page<Indicator> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        List<Indicator> indicatorList = page.getRecords();
        if (indicatorList != null && !indicatorList.isEmpty()) {
            for (Indicator indicator : indicatorList) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", indicator.getId());
                record.put("indicatorName", indicator.getIndicatorName());
                record.put("indicatorCode", indicator.getIndicatorCode());
                record.put("indicatorLogic", indicator.getIndicatorLogic());
                record.put("taskId", indicator.getTaskId());
                record.put("createTime", indicator.getCreateTime());
                record.put("updateTime", indicator.getUpdateTime());
                records.add(record);
            }
        }

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("total", total);
        pageResult.put("records", records);
        pageResult.put("pageNum", pageNum);
        pageResult.put("pageSize", pageSize);
        return Result.success("success", pageResult);
    }

    /**
     * 添加指标
     * @param request 指标添加请求
     * @return 指标ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addIndicator(AddIndicatorRequest request) {
        // 检查指标编码是否已存在
        Indicator existingIndicator = getOne(new LambdaQueryWrapper<Indicator>()
                .eq(Indicator::getIndicatorCode, request.getIndicatorCode()));
        if (existingIndicator != null) {
            return Result.failed("指标编码已存在");
        }

        try {
            Indicator newIndicator = new Indicator();
            newIndicator.setIndicatorName(request.getIndicatorName());
            newIndicator.setIndicatorCode(request.getIndicatorCode());
            newIndicator.setIndicatorLogic(request.getIndicatorLogic());
            newIndicator.setTaskId(request.getTaskId());
            newIndicator.setCreateTime(LocalDateTime.now());

            boolean saved = save(newIndicator);
            if (!saved) {
                return Result.failed("指标新增失败");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", newIndicator.getId());
            return Result.success("success", data);
        } catch (Exception e) {
            log.error("新增指标失败", e);
            return Result.failed("新增指标失败");
        }
    }

    /**
     * 更新指标
     * @param request 指标更新请求
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateIndicator(UpdateIndicatorRequest request) {
        try {
            Indicator indicator = indicatorMapper.selectById(request.getId());
            if (indicator == null) {
                return Result.failed(404, "指标不存在");
            }

            // 检查指标编码是否与其他指标冲突
            Indicator existingIndicator = getOne(new LambdaQueryWrapper<Indicator>()
                    .eq(Indicator::getIndicatorCode, request.getIndicatorCode())
                    .ne(Indicator::getId, request.getId()));
            if (existingIndicator != null) {
                return Result.failed("指标编码已被其他指标使用");
            }

            indicator.setIndicatorName(request.getIndicatorName());
            indicator.setIndicatorCode(request.getIndicatorCode());
            indicator.setIndicatorLogic(request.getIndicatorLogic());
            indicator.setTaskId(request.getTaskId());
            indicator.setUpdateTime(LocalDateTime.now());

            boolean updated = updateById(indicator);
            if (updated) {
                return Result.success("success");
            } else {
                return Result.failed(500, "指标更新失败");
            }
        } catch (Exception e) {
            log.error("更新指标失败", e);
            return Result.failed(500, "更新指标失败");
        }
    }

    /**
     * 查询指标详情
     * @param id 指标ID
     * @return 指标详情
     */
    @Override
    public Result<?> getIndicatorDetail(Long id) {
        Indicator indicator = indicatorMapper.selectById(id);
        if (indicator == null) {
            return Result.failed(404, "指标不存在");
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", indicator.getId());
        responseData.put("indicatorName", indicator.getIndicatorName());
        responseData.put("indicatorCode", indicator.getIndicatorCode());
        responseData.put("indicatorLogic", indicator.getIndicatorLogic());
        responseData.put("taskId", indicator.getTaskId());
        responseData.put("createTime", indicator.getCreateTime());
        responseData.put("updateTime", indicator.getUpdateTime());

        return Result.success("success", responseData);
    }

    /**
     * 删除指标
     * @param id 指标ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteIndicator(Long id) {
        try {
            Indicator indicator = indicatorMapper.selectById(id);
            if (indicator == null) {
                return Result.failed(404, "指标不存在");
            }

            // 检查指标是否被审核规则引用
            LambdaQueryWrapper<QuotaRule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(QuotaRule::getIndicatorCodes, indicator.getIndicatorCode());
            List<QuotaRule> rules = quotaRuleMapper.selectList(queryWrapper);
            
            if (!rules.isEmpty()) {
                List<String> ruleNames = rules.stream()
                    .map(QuotaRule::getQuotaName)
                    .toList();
                return Result.failed(400, 
                    "指标已被以下审核规则引用，无法删除：" + String.join(", ", ruleNames));
            }

            boolean deleted = removeById(id);
            if (!deleted) {
                return Result.failed(500, "指标删除失败");
            }
            return Result.success("success");
        } catch (Exception e) {
            log.error("删除指标失败: {}", e.getMessage(), e);
            return Result.failed(500, "指标删除失败");
        }
    }

    /**
     * 获取所有指标列表（供审核规则选择使用）
     * @return 指标列表
     */
    @Override
    public Result<?> getAllIndicators() {
        LambdaQueryWrapper<Indicator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Indicator::getId, Indicator::getIndicatorName, Indicator::getIndicatorCode);
        List<Indicator> indicatorList = list(queryWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        if (indicatorList != null) {
            for (Indicator indicator : indicatorList) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", indicator.getId());
                item.put("indicatorName", indicator.getIndicatorName());
                item.put("indicatorCode", indicator.getIndicatorCode());
                result.add(item);
            }
        }

        return Result.success("success", result);
    }
}
