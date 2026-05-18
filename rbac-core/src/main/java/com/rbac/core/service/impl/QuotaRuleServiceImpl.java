package com.rbac.core.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddQuotaRuleRequest;
import com.rbac.common.model.dto.QuotaCalculateRequest;
import com.rbac.common.model.dto.QuotaCalculateResponse;
import com.rbac.common.model.dto.UpdateQuotaRuleRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Indicator;
import com.rbac.core.domain.entity.QuotaRule;
import com.rbac.core.domain.entity.RpaDataQuery;
import com.rbac.core.domain.mapper.IndicatorMapper;
import com.rbac.core.domain.mapper.QuotaRuleMapper;
import com.rbac.core.domain.mapper.RpaDataQueryMapper;
import com.rbac.core.service.CozeClientService;
import com.rbac.core.service.QuotaRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaRuleServiceImpl extends ServiceImpl<QuotaRuleMapper, QuotaRule> implements QuotaRuleService {

    private final QuotaRuleMapper quotaRuleMapper;
    private final IndicatorMapper indicatorMapper;
    private final RpaDataQueryMapper rpaDataQueryMapper;
    private final CozeClientService cozeClientService;

    @Override
    public Result<?> pageQuotaRuleList(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<QuotaRule> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(QuotaRule::getQuotaName, keyword);
        }

        long total = 0L;
        try {
            total = count(queryWrapper);
        } catch (Exception e) {
            log.error("统计数据计算失败: {}", e.getMessage());
        }

        Page<QuotaRule> page = new Page<>(pageNum, pageSize);
        try {
            page = page(page, queryWrapper);
        } catch (Exception e) {
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        List<QuotaRule> quotaRuleList = page.getRecords();
        if (quotaRuleList != null && !quotaRuleList.isEmpty()) {
            for (QuotaRule quotaRule : quotaRuleList) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", quotaRule.getId());
                record.put("quotaName", quotaRule.getQuotaName());
                record.put("indicatorCodes", quotaRule.getIndicatorCodes());
                record.put("conditions", quotaRule.getConditions());
                record.put("quotaCalculation", quotaRule.getQuotaCalculation());
                record.put("resultVarName", quotaRule.getResultVarName());
                record.put("outputTemplate", quotaRule.getOutputTemplate());
                record.put("calculatedResult", quotaRule.getCalculatedResult());
                record.put("createTime", quotaRule.getCreateTime());
                record.put("updateTime", quotaRule.getUpdateTime());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addQuotaRule(AddQuotaRuleRequest request) {
        try {
            QuotaRule quotaRule = new QuotaRule();
            quotaRule.setQuotaName(request.getQuotaName());
            quotaRule.setIndicatorCodes(request.getIndicatorCodes());
            quotaRule.setConditions(request.getConditions());
            quotaRule.setQuotaCalculation(request.getQuotaCalculation());
            quotaRule.setResultVarName(request.getResultVarName());
            quotaRule.setOutputTemplate(request.getOutputTemplate());
            quotaRule.setCreateTime(LocalDateTime.now());

            boolean saved = save(quotaRule);
            if (!saved) {
                return Result.failed("新增审核规则失败");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", quotaRule.getId());
            return Result.success("success", data);
        } catch (Exception e) {
            log.error("新增审核规则失败", e);
            return Result.failed("新增审核规则失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateQuotaRule(UpdateQuotaRuleRequest request) {
        try {
            QuotaRule quotaRule = quotaRuleMapper.selectById(request.getId());
            if (quotaRule == null) {
                return Result.failed(404, "审核规则不存在");
            }

            quotaRule.setQuotaName(request.getQuotaName());
            quotaRule.setIndicatorCodes(request.getIndicatorCodes());
            quotaRule.setConditions(request.getConditions());
            quotaRule.setQuotaCalculation(request.getQuotaCalculation());
            quotaRule.setResultVarName(request.getResultVarName());
            quotaRule.setOutputTemplate(request.getOutputTemplate());
            quotaRule.setUpdateTime(LocalDateTime.now());

            boolean updated = updateById(quotaRule);
            if (updated) {
                return Result.success("success");
            } else {
                return Result.failed(500, "更新审核规则失败");
            }
        } catch (Exception e) {
            log.error("更新审核规则失败", e);
            return Result.failed(500, "更新审核规则失败");
        }
    }

    @Override
    public Result<?> getQuotaRuleDetail(Long id) {
        QuotaRule quotaRule = quotaRuleMapper.selectById(id);
        if (quotaRule == null) {
            return Result.failed(404, "审核规则不存在");
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", quotaRule.getId());
        responseData.put("quotaName", quotaRule.getQuotaName());
        responseData.put("indicatorCodes", quotaRule.getIndicatorCodes());
        responseData.put("conditions", quotaRule.getConditions());
        responseData.put("quotaCalculation", quotaRule.getQuotaCalculation());
        responseData.put("resultVarName", quotaRule.getResultVarName());
        responseData.put("outputTemplate", quotaRule.getOutputTemplate());
        responseData.put("calculatedResult", quotaRule.getCalculatedResult());
        responseData.put("createTime", quotaRule.getCreateTime());
        responseData.put("updateTime", quotaRule.getUpdateTime());

        return Result.success("success", responseData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteQuotaRule(Long id) {
        try {
            QuotaRule quotaRule = quotaRuleMapper.selectById(id);
            if (quotaRule == null) {
                return Result.failed(404, "审核规则不存在");
            }

            boolean deleted = removeById(id);
            if (!deleted) {
                return Result.failed(500, "删除审核规则失败");
            }
            return Result.success("success");
        } catch (Exception e) {
            log.error("删除审核规则失败: {}", e.getMessage(), e);
            return Result.failed(500, "删除审核规则失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<QuotaCalculateResponse> calculateQuota(QuotaCalculateRequest request) {
        try {
            QuotaRule quotaRule = quotaRuleMapper.selectById(request.getQuotaRuleId());
            if (quotaRule == null) {
                return Result.failed(404, "审核规则不存在");
            }

            Map<String, Object> businessData = buildBusinessData(quotaRule.getIndicatorCodes(), request.getData());

            Map<String, Object> cozeResponse = cozeClientService.calculateQuota(
                    quotaRule.getIndicatorCodes(),
                    quotaRule.getConditions(),
                    quotaRule.getQuotaCalculation(),
                    quotaRule.getResultVarName(),
                    quotaRule.getOutputTemplate(),
                    businessData
            );

            Map<String, Object> output = parseOutputTemplate(quotaRule.getOutputTemplate(), cozeResponse);
            
            Object resultValue = output.get(quotaRule.getResultVarName());
            
            quotaRule.setCalculatedResult(resultValue != null ? resultValue.toString() : null);
            quotaRule.setUpdateTime(LocalDateTime.now());
            updateById(quotaRule);
            log.info("审核规则 {} 的计算结果已保存: {}", quotaRule.getId(), resultValue);

            QuotaCalculateResponse response = QuotaCalculateResponse.builder()
                    .quotaRuleId(quotaRule.getId())
                    .quotaRuleName(quotaRule.getQuotaName())
                    .calculatedResult(resultValue)
                    .status("success")
                    .output(output)
                    .calculatedAt(LocalDateTime.now())
                    .cozeResponse(cozeResponse)
                    .build();

            return Result.success("success", response);
        } catch (CozeClientService.CozeInsufficientCreditsException e) {
            log.error("Coze 积分不足", e);
            return Result.failed(500, e.getMessage());
        } catch (CozeClientService.CozeTimeoutException e) {
            log.error("Coze 调用超时", e);
            return Result.failed(500, e.getMessage());
        } catch (CozeClientService.CozeApiException e) {
            log.error("Coze API 错误", e);
            return Result.failed(500, e.getMessage());
        } catch (Exception e) {
            log.error("额度计算失败", e);
            return Result.failed(500, "额度计算失败: " + e.getMessage());
        }
    }

    private Map<String, Object> buildBusinessData(String indicatorCodes, Map<String, Object> inputData) {
        Map<String, Object> businessData = new HashMap<>();
        
        if (inputData != null) {
            businessData.putAll(inputData);
        }

        if (indicatorCodes != null && !indicatorCodes.isEmpty()) {
            String[] codes = indicatorCodes.split(",");
            for (String code : codes) {
                code = code.trim();
                if (!code.isEmpty()) {
                    LambdaQueryWrapper<Indicator> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Indicator::getIndicatorCode, code);
                    Indicator indicator = indicatorMapper.selectOne(queryWrapper);

                    if (indicator != null) {
                        String dataKey = code.toLowerCase();
                        
                        Map<String, Object> indicatorData = getLatestExecutionData(indicator.getTaskId(), dataKey);
                        
                        if (indicatorData != null) {
                            indicatorData.put("indicator_logic", indicator.getIndicatorLogic());
                            businessData.put(dataKey, indicatorData);
                        } else {
                            Map<String, Object> fallbackData = new HashMap<>();
                            fallbackData.put("value", inputData != null && inputData.containsKey(dataKey) 
                                    ? inputData.get(dataKey) : 0.0);
                            fallbackData.put("source", "taskId=" + indicator.getTaskId());
                            fallbackData.put("raw_data", new HashMap<>());
                            fallbackData.put("indicator_logic", indicator.getIndicatorLogic());
                            businessData.put(dataKey, fallbackData);
                            log.warn("指标 {} 未找到最新执行记录，使用默认值", code);
                        }
                    }
                }
            }
        }

        return businessData;
    }
    
    private Map<String, Object> getLatestExecutionData(Long taskId, String dataKey) {
        if (taskId == null) {
            return null;
        }
        
        try {
            LambdaQueryWrapper<RpaDataQuery> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RpaDataQuery::getTaskId, taskId)
                       .eq(RpaDataQuery::getDeleted, 0)
                       .orderByDesc(RpaDataQuery::getCreateTime)
                       .last("LIMIT 1");
            
            RpaDataQuery latestData = rpaDataQueryMapper.selectOne(queryWrapper);
            
            if (latestData == null) {
                log.warn("taskId {} 未找到查询数据", taskId);
                return null;
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            
            if (latestData.getBusinessData() != null && !latestData.getBusinessData().isEmpty()) {
                try {
                    Map<String, Object> dataMap = JSON.parseObject(
                        latestData.getBusinessData(), 
                        new com.alibaba.fastjson2.TypeReference<Map<String, Object>>() {}
                    );
                    
                    if (dataMap != null) {
                        Object indicatorValue = extractIndicatorValue(dataMap, dataKey);
                        
                        resultMap.put("value", indicatorValue);
                        resultMap.put("source", "taskId=" + taskId);
                        resultMap.put("raw_data", dataMap);
                        resultMap.put("collectionId", dataMap.get("collectionId"));
                        resultMap.put("enterpriseName", latestData.getEnterpriseName());
                        resultMap.put("taxNo", latestData.getTaxNo());
                        resultMap.put("taskId", latestData.getTaskId());
                        
                        return resultMap;
                    }
                    
                } catch (Exception e) {
                    log.error("解析 businessData JSON 失败: {}", latestData.getBusinessData(), e);
                    return null;
                }
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("获取最新查询数据失败，taskId: {}, dataKey: {}", taskId, dataKey, e);
            return null;
        }
    }
    
    private Object extractIndicatorValue(Map<String, Object> dataMap, String dataKey) {
        if (dataMap.containsKey(dataKey)) {
            return dataMap.get(dataKey);
        }
        
        if (dataMap.containsKey("result")) {
            Object resultObj = dataMap.get("result");
            if (resultObj instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) resultObj;
                if (resultMap.containsKey(dataKey)) {
                    return resultMap.get(dataKey);
                }
                for (String key : resultMap.keySet()) {
                    Object value = resultMap.get(key);
                    if (value instanceof Number) {
                        return value;
                    }
                }
            }
        }
        
        for (String key : dataMap.keySet()) {
            Object value = dataMap.get(key);
            if (value instanceof Number) {
                return value;
            }
        }
        
        return null;
    }

    private Map<String, Object> parseOutputTemplate(String template, Map<String, Object> data) {
        try {
            if (data.containsKey("outputs")) {
                Object outputs = data.get("outputs");
                if (outputs instanceof Map) {
                    return (Map<String, Object>) outputs;
                }
            }

            if (data.containsKey("answer")) {
                String answer = (String) data.get("answer");
                try {
                    return JSON.parseObject(answer, Map.class);
                } catch (Exception e) {
                    log.warn("解析 answer 失败，返回原始数据");
                }
            }

            if (data.containsKey("status") && data.containsKey("creditLimit")) {
                return data;
            }

            return data;
        } catch (Exception e) {
            log.error("解析输出模板失败", e);
            return data;
        }
    }
}
