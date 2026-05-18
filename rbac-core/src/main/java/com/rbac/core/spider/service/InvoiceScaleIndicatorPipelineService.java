package com.rbac.core.spider.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.core.domain.entity.RpaDataCollection;
import com.rbac.core.domain.entity.RpaDataParsing;
import com.rbac.core.domain.entity.RpaDataProcessing;
import com.rbac.core.domain.entity.RpaDataQuery;
import com.rbac.core.domain.mapper.RpaDataCollectionMapper;
import com.rbac.core.domain.mapper.RpaDataParsingMapper;
import com.rbac.core.domain.mapper.RpaDataProcessingMapper;
import com.rbac.core.domain.mapper.RpaDataQueryMapper;
import com.rbac.core.spider.CollectedInvoiceRow;
import com.rbac.core.spider.CollectedPayload;
import com.rbac.core.spider.PlaywrightSpiderWebClient;
import com.rbac.common.utils.IndicatorDateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvoiceScaleIndicatorPipelineService {

    public static final String INDICATOR_CODE = "inv_f1_12m_down_sale_jshj_sum_teach";

    private final RpaDataCollectionMapper collectionMapper;
    private final RpaDataParsingMapper parsingMapper;
    private final RpaDataProcessingMapper processingMapper;
    private final RpaDataQueryMapper queryMapper;
    private final ObjectMapper objectMapper;

    @Value("${spider.web.base-url:http://localhost:3000}")
    private String defaultBaseUrl;

    @Value("${spider.web.headless:true}")
    private boolean defaultHeadless;

    public InvoiceScaleIndicatorPipelineService(RpaDataCollectionMapper collectionMapper,
                                                RpaDataParsingMapper parsingMapper,
                                                RpaDataProcessingMapper processingMapper,
                                                RpaDataQueryMapper queryMapper,
                                                ObjectMapper objectMapper) {
        this.collectionMapper = collectionMapper;
        this.parsingMapper = parsingMapper;
        this.processingMapper = processingMapper;
        this.queryMapper = queryMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, Object> run(Long taskId, String enterpriseName, String appDate) {
        // 构建请求参数
        Map<String, Object> request = new HashMap<>();
        request.put("taskId", taskId);
        request.put("enterpriseName", enterpriseName);
        request.put("appDate", appDate);
        request.put("baseUrl", defaultBaseUrl);
        request.put("headless", defaultHeadless);
        
        // 采集
        RpaDataCollection collection = collectStep(request);
        // 解析
        RpaDataParsing parsing = parseStep(collection);
        // 加工
        RpaDataProcessing processing = processStep(parsing);
        // 完成落库
        RpaDataQuery query = completeStep(collection, parsing, processing);

        BigDecimal result = readProcessedSaleSum(processing.getProcessedData());
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("collectionId", collection.getId());
        response.put("parsingId", parsing.getId());
        response.put("processingId", processing.getId());
        response.put("queryId", query.getId());
        response.put("indicatorCode", INDICATOR_CODE);
        response.put("result", result);
        return response;
    }

    /**
     * 采集：按说明文档驱动页面操作，抓取原始字段并入库到 rpa_data_collection
     */
    public RpaDataCollection collectStep(Map<String, Object> request) {
        LocalDateTime now = LocalDateTime.now();

        String baseUrl = StrUtil.blankToDefault((String) request.get("baseUrl"), defaultBaseUrl);
        boolean headless = request.get("headless") != null ? (boolean) request.get("headless") : defaultHeadless;

        RpaDataCollection entity = new RpaDataCollection();
        entity.setTaskId((Long) request.get("taskId"));
        entity.setEnterpriseName((String) request.get("enterpriseName"));
        entity.setDataSource("spider_web");
        entity.setCollectionTime(now);
        entity.setStatus("collected");
        entity.setDeleted(0);

        try {
            PlaywrightSpiderWebClient client = new PlaywrightSpiderWebClient();
            CollectedPayload payload = client.collect(baseUrl, headless, (String) request.get("enterpriseName"), (String) request.get("appDate"));

            entity.setTaxNo(payload.getTaxNo());
            entity.setRawData(toJson(payload));
            collectionMapper.insert(entity);
            return entity;
        } catch (Exception e) {
            entity.setStatus("failed");
            entity.setErrorMessage(truncate(e.getMessage(), 2000));
            entity.setRawData(null);
            collectionMapper.insert(entity);
            throw e;
        }
    }

    /**
     * 按采集ID执行解析（供分步接口调用）
     */
    public RpaDataParsing parseStep(Long collectionId) {
        RpaDataCollection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            throw new IllegalArgumentException("采集记录不存在: collectionId=" + collectionId);
        }
        return parseStep(collection);
    }

    /**
     * 解析：从 raw_data 解析成结构化数据并入库到 rpa_data_parsing，同时更新 collection.status
     */
    public RpaDataParsing parseStep(RpaDataCollection collection) {
        updateCollectionStatus(collection.getId(), "parsing", null);

        RpaDataParsing parsing = new RpaDataParsing();
        parsing.setCollectionId(collection.getId());
        parsing.setTaskId(collection.getTaskId());
        parsing.setTaxNo(collection.getTaxNo());
        parsing.setEnterpriseName(collection.getEnterpriseName());
        parsing.setParsingTime(LocalDateTime.now());
        parsing.setStatus("parsed");
        parsing.setDeleted(0);

        try {
            CollectedPayload payload = objectMapper.readValue(collection.getRawData(), CollectedPayload.class);
            Map<String, Object> parsed = new LinkedHashMap<>();
            parsed.put("enterpriseName", payload.getEnterpriseName());
            parsed.put("taxNo", payload.getTaxNo());
            parsed.put("uscCode", payload.getUscCode());
            parsed.put("appDate", payload.getAppDate());

            LocalDate appDate = IndicatorDateUtil.parseAppDate(payload.getAppDate());
            List<Map<String, Object>> invoices = new ArrayList<>();
            for (CollectedInvoiceRow row : payload.getInvoices()) {
                Map<String, Object> inv = new LinkedHashMap<>();
                inv.put("sign", row.getSign());
                inv.put("state", row.getState());
                inv.put("invoiceTime", row.getInvoiceTime());
                inv.put("jshjText", row.getJshjText());

                // 尝试解析，失败也保留原字段
                if (IndicatorDateUtil.tryParseInvoiceTime(row.getInvoiceTime())) {
                    LocalDateTime invoiceTime = IndicatorDateUtil.parseInvoiceTime(row.getInvoiceTime());
                    inv.put("invoiceTimeParsed", invoiceTime.toString());
                    int diffMonths = (appDate.getYear() - invoiceTime.getYear()) * 12 + (appDate.getMonthValue() - invoiceTime.getMonthValue());
                    inv.put("monthDiffToAppDate", diffMonths);
                }
                invoices.add(inv);
            }
            parsed.put("invoices", invoices);
            parsing.setParsedData(toJson(parsed));

            parsingMapper.insert(parsing);
            updateCollectionStatus(collection.getId(), "parsed", null);
            return parsing;
        } catch (Exception e) {
            parsing.setStatus("failed");
            parsing.setErrorMessage(truncate(e.getMessage(), 2000));
            parsingMapper.insert(parsing);
            updateCollectionStatus(collection.getId(), "failed", truncate(e.getMessage(), 2000));
            throw new RuntimeException(e);
        }
    }

    /**
     * 按解析ID执行加工（供分步接口调用）
     */
    public RpaDataProcessing processStep(Long parsingId) {
        RpaDataParsing parsing = parsingMapper.selectById(parsingId);
        if (parsing == null) {
            throw new IllegalArgumentException("解析记录不存在: parsingId=" + parsingId);
        }
        return processStep(parsing);
    }

    /**
     * 加工：按指标口径过滤并求和，入库到 rpa_data_processing，同时更新 parsing.status
     */
    public RpaDataProcessing processStep(RpaDataParsing parsing) {
        updateParsingStatus(parsing.getId(), "processing", null);

        RpaDataProcessing processing = new RpaDataProcessing();
        processing.setParsingId(parsing.getId());
        processing.setTaskId(parsing.getTaskId());
        processing.setTaxNo(parsing.getTaxNo());
        processing.setEnterpriseName(parsing.getEnterpriseName());
        processing.setProcessingTime(LocalDateTime.now());
        processing.setStatus("processed");
        processing.setDeleted(0);

        try {
            Map<String, Object> parsed = readJsonMap(parsing.getParsedData());
            String appDateStr = String.valueOf(parsed.get("appDate"));
            LocalDate appDate = IndicatorDateUtil.parseAppDate(appDateStr);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> invoices = (List<Map<String, Object>>) parsed.get("invoices");
            BigDecimal sum = BigDecimal.ZERO;
            int total = 0;
            int matched = 0;
            List<Map<String, Object>> matchedInvoices = new ArrayList<>();

            for (Map<String, Object> inv : invoices) {
                total++;
                String sign = String.valueOf(inv.get("sign"));
                String state = String.valueOf(inv.get("state"));
                String invoiceTimeStr = String.valueOf(inv.get("invoiceTime"));
                String jshjText = String.valueOf(inv.get("jshjText"));

                if (!"销项".equals(sign)) {
                    continue;
                }
                if (!"正常".equals(state)) {
                    continue;
                }
                if (!IndicatorDateUtil.tryParseInvoiceTime(invoiceTimeStr)) {
                    continue;
                }

                LocalDateTime invoiceTime = IndicatorDateUtil.parseInvoiceTime(invoiceTimeStr);
                if (!IndicatorDateUtil.isInvoiceIn1To12MonthsBeforeAppDateExcludingCurrentMonth(appDate, invoiceTime)) {
                    continue;
                }

                BigDecimal jshj = parseMoney(jshjText);
                sum = sum.add(jshj);
                matched++;

                Map<String, Object> mi = new LinkedHashMap<>();
                mi.put("sign", sign);
                mi.put("state", state);
                mi.put("invoiceTime", invoiceTimeStr);
                mi.put("jshj", jshj);
                matchedInvoices.add(mi);
            }

            BigDecimal result = sum;
            if (result == null) {
                result = BigDecimal.ZERO;
            }
            result = result.setScale(5, RoundingMode.HALF_UP);

            Map<String, Object> processed = new LinkedHashMap<>();
            processed.put("indicatorCode", INDICATOR_CODE);
            processed.put("sale_jshj_sum", result);
            processed.put("appDate", appDateStr);
            processed.put("windowRule", "近1-12个月(不含当月)");

            Map<String, Object> validation = new LinkedHashMap<>();
            validation.put("invoiceTotal", total);
            validation.put("invoiceMatched", matched);
            validation.put("matchedInvoices", matchedInvoices);

            processing.setProcessedData(toJson(processed));
            processing.setValidationResult(toJson(validation));

            processingMapper.insert(processing);
            updateParsingStatus(parsing.getId(), "processed", null);
            return processing;
        } catch (Exception e) {
            processing.setStatus("failed");
            processing.setErrorMessage(truncate(e.getMessage(), 2000));
            processingMapper.insert(processing);
            updateParsingStatus(parsing.getId(), "failed", truncate(e.getMessage(), 2000));
            throw new RuntimeException(e);
        }
    }

    /**
     * 按三个ID执行落库（供分步接口调用）
     */
    public RpaDataQuery completeStep(Long collectionId, Long parsingId, Long processingId) {
        RpaDataCollection collection = collectionMapper.selectById(collectionId);
        RpaDataParsing parsing = parsingMapper.selectById(parsingId);
        RpaDataProcessing processing = processingMapper.selectById(processingId);
        if (collection == null || parsing == null || processing == null) {
            throw new IllegalArgumentException("采集/解析/加工记录不存在: collectionId=" + collectionId + ", parsingId=" + parsingId + ", processingId=" + processingId);
        }
        return completeStep(collection, parsing, processing);
    }

    /**
     * 完成：将最终业务数据写入 rpa_data_query，同时将 processing.status 置为 exported
     */
    public RpaDataQuery completeStep(RpaDataCollection collection, RpaDataParsing parsing, RpaDataProcessing processing) {
        RpaDataQuery query = new RpaDataQuery();
        query.setTaskId(collection.getTaskId());
        query.setTaxNo(collection.getTaxNo());
        query.setEnterpriseName(collection.getEnterpriseName());
        query.setCategoryId(null);
        query.setDataStatus(1); // 1: available (可用)
        query.setDeleted(0);

        try {
            Map<String, Object> business = new LinkedHashMap<>();
            business.put("indicatorCode", INDICATOR_CODE);
            business.put("taskId", collection.getTaskId());
            business.put("enterpriseName", collection.getEnterpriseName());
            business.put("taxNo", collection.getTaxNo());

            Map<String, Object> processed = readJsonMap(processing.getProcessedData());
            business.put("result", processed);
            business.put("collectionId", collection.getId());
            business.put("parsingId", parsing.getId());
            business.put("processingId", processing.getId());

            query.setBusinessData(toJson(business));
            queryMapper.insert(query);

            updateProcessingStatus(processing.getId(), "exported", null);
            return query;
        } catch (Exception e) {
            updateProcessingStatus(processing.getId(), "failed", truncate(e.getMessage(), 2000));
            throw new RuntimeException(e);
        }
    }

    private void updateCollectionStatus(Long collectionId, String status, String errorMessage) {
        UpdateWrapper<RpaDataCollection> uw = new UpdateWrapper<>();
        uw.eq("id", collectionId).set("status", status);
        if (errorMessage != null) {
            uw.set("error_message", errorMessage);
        }
        collectionMapper.update(null, uw);
    }

    private void updateParsingStatus(Long parsingId, String status, String errorMessage) {
        UpdateWrapper<RpaDataParsing> uw = new UpdateWrapper<>();
        uw.eq("id", parsingId).set("status", status);
        if (errorMessage != null) {
            uw.set("error_message", errorMessage);
        }
        parsingMapper.update(null, uw);
    }

    private void updateProcessingStatus(Long processingId, String status, String errorMessage) {
        UpdateWrapper<RpaDataProcessing> uw = new UpdateWrapper<>();
        uw.eq("id", processingId).set("status", status);
        if (errorMessage != null) {
            uw.set("error_message", errorMessage);
        }
        processingMapper.update(null, uw);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal parseMoney(String text) {
        if (text == null) {
            return BigDecimal.ZERO;
        }
        String cleaned = text.trim().replace(",", "");
        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }

    private BigDecimal readProcessedSaleSum(String processedDataJson) {
        try {
            Map<String, Object> m = readJsonMap(processedDataJson);
            Object v = m.get("sale_jshj_sum");
            if (v == null) return BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP);
            return new BigDecimal(String.valueOf(v)).setScale(5, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP);
        }
    }

    private Map<String, Object> readJsonMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }
}

