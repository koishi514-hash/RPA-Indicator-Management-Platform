package com.rbac.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rbac.common.utils.IndicatorDateUtil;
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
import com.rbac.core.spider.service.InvoiceScaleIndicatorPipelineService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 四步流水线测试：采集(Playwright)、解析(RestTemplate+落库)、加工(RestTemplate+落库)、完成落库(RestTemplate+落库)。
 * 每步单独落库到对应表，表结构见 DDL（tax_no, enterprise_name）。
 *
 * 运行前请确保：
 * 1. MySQL 中 rpa_management 库已执行 DDL（rpa_data_collection/parsing/processing/query 含 tax_no, enterprise_name）。
 * 2. spider_web 已启动：cd spider_web && npm run dev（默认 http://localhost:3000），否则 testCollect 会超时。
 * 3. 四个测试按 @Order(1)～(4) 顺序执行，先采集再解析再加工再落库。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FourStepPipelineTest {

    private static final String SPIDER_WEB_BASE_URL = "http://study.zmyfrank.com:18010/spider/#";
    private static final Long TASK_ID = 1L;
    private static final String ENTERPRISE_NAME = "重庆某某科技有限公司";
    private static final String APP_DATE = "2024-02-06";
    private static final String INDICATOR_CODE = InvoiceScaleIndicatorPipelineService.INDICATOR_CODE;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RpaDataCollectionMapper collectionMapper;

    @Autowired
    private RpaDataParsingMapper parsingMapper;

    @Autowired
    private RpaDataProcessingMapper processingMapper;

    @Autowired
    private RpaDataQueryMapper queryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/spider";
    }

    // --- 步骤1：采集（仅 Playwright），落库 rpa_data_collection ---
    @Test
    @Order(1)
    void testCollect() throws Exception {
        PlaywrightSpiderWebClient client = new PlaywrightSpiderWebClient();
        CollectedPayload payload = client.collect(SPIDER_WEB_BASE_URL, true, ENTERPRISE_NAME, APP_DATE);

        RpaDataCollection row = new RpaDataCollection();
        row.setTaskId(TASK_ID);
        row.setTaxNo(payload.getTaxNo());
        row.setEnterpriseName(payload.getEnterpriseName());
        row.setRawData(objectMapper.writeValueAsString(payload));
        row.setDataSource("spider_web");
        row.setCollectionTime(LocalDateTime.now());
        row.setStatus("collected");
        row.setDeleted(0);

        collectionMapper.insert(row);

        assertThat(row.getId()).isNotNull();
        assertThat(payload.getTaxNo()).isNotBlank();
        assertThat(payload.getUscCode()).isNotBlank();
        assertThat(payload.getAppDate()).isNotBlank();
        assertThat(payload.getInvoices()).isNotEmpty();
    }

    // --- 步骤2：解析（RestTemplate 拉取采集结果），落库 rpa_data_parsing ---
    @Test
    @Order(2)
    void testParse() throws Exception {
        RpaDataCollection collection = restTemplate.getForObject(
                baseUrl() + "/collection/latest?taskId=" + TASK_ID, RpaDataCollection.class);
        assertThat(collection).isNotNull();
        assertThat(collection.getRawData()).isNotBlank();

        CollectedPayload payload = objectMapper.readValue(collection.getRawData(), CollectedPayload.class);
        LocalDate appDate = IndicatorDateUtil.parseAppDate(payload.getAppDate());

        Map<String, Object> parsed = new LinkedHashMap<>();
        parsed.put("enterpriseName", payload.getEnterpriseName());
        parsed.put("taxNo", payload.getTaxNo());
        parsed.put("uscCode", payload.getUscCode());
        parsed.put("appDate", payload.getAppDate());

        List<Map<String, Object>> invoices = new ArrayList<>();
        for (CollectedInvoiceRow row : payload.getInvoices()) {
            Map<String, Object> inv = new LinkedHashMap<>();
            inv.put("sign", row.getSign());
            inv.put("state", row.getState());
            inv.put("invoiceTime", row.getInvoiceTime());
            inv.put("jshjText", row.getJshjText());
            if (IndicatorDateUtil.tryParseInvoiceTime(row.getInvoiceTime())) {
                LocalDateTime invoiceTime = IndicatorDateUtil.parseInvoiceTime(row.getInvoiceTime());
                inv.put("invoiceTimeParsed", invoiceTime.toString());
                int diff = (appDate.getYear() - invoiceTime.getYear()) * 12 + (appDate.getMonthValue() - invoiceTime.getMonthValue());
                inv.put("monthDiffToAppDate", diff);
            }
            invoices.add(inv);
        }
        parsed.put("invoices", invoices);

        RpaDataParsing parsing = new RpaDataParsing();
        parsing.setCollectionId(collection.getId());
        parsing.setTaskId(collection.getTaskId());
        parsing.setTaxNo(collection.getTaxNo());
        parsing.setEnterpriseName(collection.getEnterpriseName());
        parsing.setParsedData(objectMapper.writeValueAsString(parsed));
        parsing.setParsingTime(LocalDateTime.now());
        parsing.setStatus("parsed");
        parsing.setDeleted(0);

        parsingMapper.insert(parsing);

        assertThat(parsing.getId()).isNotNull();
    }

    // --- 步骤3：加工（RestTemplate 拉取解析结果），按指标口径过滤求和，落库 rpa_data_processing ---
    @Test
    @Order(3)
    void testProcess() throws Exception {
        RpaDataParsing parsing = restTemplate.getForObject(
                baseUrl() + "/parsing/latest?taskId=" + TASK_ID, RpaDataParsing.class);
        assertThat(parsing).isNotNull();
        assertThat(parsing.getParsedData()).isNotBlank();

        Map<String, Object> parsed = objectMapper.readValue(parsing.getParsedData(),
                new TypeReference<Map<String, Object>>() {});
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

            if (!"销项".equals(sign) || !"正常".equals(state)) continue;
            if (!IndicatorDateUtil.tryParseInvoiceTime(invoiceTimeStr)) continue;

            LocalDateTime invoiceTime = IndicatorDateUtil.parseInvoiceTime(invoiceTimeStr);
            if (!IndicatorDateUtil.isInvoiceIn1To12MonthsBeforeAppDateExcludingCurrentMonth(appDate, invoiceTime)) continue;

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

        BigDecimal result = sum == null ? BigDecimal.ZERO : sum.setScale(5, RoundingMode.HALF_UP);

        Map<String, Object> processed = new LinkedHashMap<>();
        processed.put("indicatorCode", INDICATOR_CODE);
        processed.put("sale_jshj_sum", result);
        processed.put("appDate", appDateStr);
        processed.put("windowRule", "近1-12个月(不含当月)");

        Map<String, Object> validation = new LinkedHashMap<>();
        validation.put("invoiceTotal", total);
        validation.put("invoiceMatched", matched);
        validation.put("matchedInvoices", matchedInvoices);

        RpaDataProcessing processing = new RpaDataProcessing();
        processing.setParsingId(parsing.getId());
        processing.setTaskId(parsing.getTaskId());
        processing.setTaxNo(parsing.getTaxNo());
        processing.setEnterpriseName(parsing.getEnterpriseName());
        processing.setProcessedData(objectMapper.writeValueAsString(processed));
        processing.setValidationResult(objectMapper.writeValueAsString(validation));
        processing.setProcessingTime(LocalDateTime.now());
        processing.setStatus("processed");
        processing.setDeleted(0);

        processingMapper.insert(processing);

        assertThat(processing.getId()).isNotNull();
        assertThat(result).isNotNull();
    }

    // --- 步骤4：完成落库（RestTemplate 拉取加工结果），落库 rpa_data_query ---
    @Test
    @Order(4)
    void testComplete() throws Exception {
        RpaDataProcessing processing = restTemplate.getForObject(
                baseUrl() + "/processing/latest?taskId=" + TASK_ID, RpaDataProcessing.class);
        assertThat(processing).isNotNull();
        assertThat(processing.getProcessedData()).isNotBlank();

        RpaDataCollection collection = restTemplate.getForObject(
                baseUrl() + "/collection/latest?taskId=" + TASK_ID, RpaDataCollection.class);
        RpaDataParsing parsing = restTemplate.getForObject(
                baseUrl() + "/parsing/latest?taskId=" + TASK_ID, RpaDataParsing.class);
        assertThat(collection).isNotNull();
        assertThat(parsing).isNotNull();

        Map<String, Object> business = new LinkedHashMap<>();
        business.put("indicatorCode", INDICATOR_CODE);
        business.put("taskId", collection.getTaskId());
        business.put("enterpriseName", collection.getEnterpriseName());
        business.put("taxNo", collection.getTaxNo());
        business.put("result", objectMapper.readValue(processing.getProcessedData(), Map.class));
        business.put("collectionId", collection.getId());
        business.put("parsingId", parsing.getId());
        business.put("processingId", processing.getId());

        RpaDataQuery query = new RpaDataQuery();
        query.setTaskId(collection.getTaskId());
        query.setTaxNo(collection.getTaxNo());
        query.setEnterpriseName(collection.getEnterpriseName());
        query.setCategoryId(null);
        query.setBusinessData(objectMapper.writeValueAsString(business));
        query.setDataStatus("available");
        query.setDeleted(0);

        queryMapper.insert(query);

        assertThat(query.getId()).isNotNull();
    }

    private static BigDecimal parseMoney(String text) {
        if (text == null) return BigDecimal.ZERO;
        String cleaned = text.trim().replace(",", "");
        if (cleaned.isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
