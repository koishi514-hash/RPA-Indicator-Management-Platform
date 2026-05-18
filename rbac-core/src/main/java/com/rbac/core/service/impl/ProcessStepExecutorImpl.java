package com.rbac.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.core.domain.mapper.RpaDataQueryMapper;
import com.rbac.core.service.ProcessStepExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.rbac.core.domain.entity.ExecutionStep;
import com.rbac.core.domain.entity.ProcessStep;
import com.rbac.core.domain.entity.RpaDataParsing;
import com.rbac.core.domain.mapper.RpaDataCollectionMapper;
import com.rbac.core.domain.mapper.RpaDataParsingMapper;
import com.rbac.core.domain.mapper.RpaDataProcessingMapper;
import com.microsoft.playwright.Page;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * 流程步骤执行器实现类
 * 支持Groovy和JavaScript脚本执行
 * 实现完整的变量注入机制和异常处理
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessStepExecutorImpl implements ProcessStepExecutor {

    private final JdbcTemplate jdbcTemplate;
    private final RpaDataCollectionMapper collectionMapper;
    private final RpaDataParsingMapper parsingMapper;
    private final RpaDataProcessingMapper processingMapper;
    private final RpaDataQueryMapper queryMapper;
    private final ObjectMapper objectMapper;
    
    // 存储Page对象的缓存，用于在同一执行流程中共享
    private final Map<String, Page> pageCache = new HashMap<>();

    /**
     * 执行流程步骤
     * @param step 流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    @Override
    public ExecutionStep executeStep(ProcessStep step, Map<String, Object> context) {
        if (step == null) {
            log.error("流程步骤为空");
            return createFailedExecutionStep("流程步骤为空");
        }
        String stepType = step.getStepType();
        if ("java".equals(stepType) || "JAVA".equals(stepType)) {
            return runJavaStep(step, context);
        } else if ("script".equals(stepType) || "JavaScript".equals(stepType)) {
            return runScriptStep(step, context);
        } else {
            log.error("未支持的步骤类型: {}", stepType);
            return createFailedExecutionStep("未支持的步骤类型: " + stepType);
        }
    }
    
    /**
     * 获取或创建Page对象
     * @param executionId 执行ID
     * @return Page对象
     */
    private Page getPage(String executionId) {
        return pageCache.get(executionId);
    }
    
    /**
     * 存储Page对象
     * @param executionId 执行ID
     * @param page Page对象
     */
    private void setPage(String executionId, Page page) {
        pageCache.put(executionId, page);
    }
    
    /**
     * 清理Page对象
     * @param executionId 执行ID
     */
    private void clearPage(String executionId) {
        Page page = pageCache.remove(executionId);
        if (page != null) {
            try {
                page.close();
            } catch (Exception e) {
                log.error("关闭Page对象失败", e);
            }
        }
    }
    
    /**
     * 创建绑定变量
     * @param context 执行上下文
     * @param step 流程步骤
     * @return 绑定变量
     */
    private SimpleBindings createBindings(Map<String, Object> context, ProcessStep step) {
        SimpleBindings bindings = new SimpleBindings();
        
        // 注入上下文变量
        if (context != null) {
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                bindings.put(entry.getKey(), entry.getValue());
                log.debug("注入上下文变量: {} = {}", entry.getKey(), entry.getValue());
            }
        }
        
        // 注入jdbcTemplate
        bindings.put("jdbcTemplate", jdbcTemplate);
        log.debug("注入变量: jdbcTemplate");
        
        // 注入Mapper
        bindings.put("collectionMapper", collectionMapper);
        bindings.put("parsingMapper", parsingMapper);
        bindings.put("processingMapper", processingMapper);
        bindings.put("queryMapper", queryMapper);
        bindings.put("objectMapper", objectMapper);
        log.debug("注入Mapper变量");
        
        // 注入步骤相关变量
        bindings.put("stepId", step.getId());
        bindings.put("stepName", step.getStepName());
        bindings.put("stepOrder", step.getStepOrder());
        bindings.put("stepType", step.getStepType());
        log.debug("注入步骤变量: stepId={}, stepName={}, stepOrder={}, stepType={}", 
                  step.getId(), step.getStepName(), step.getStepOrder(), step.getStepType());
        
        // 注入环境变量（示例）
        bindings.put("baseUrl", getEnvironmentVariable("BASE_URL", "http://localhost:3000"));
        bindings.put("playwrightTimeoutMs", getEnvironmentVariable("PLAYWRIGHT_TIMEOUT_MS", "30000"));
        log.debug("注入环境变量: baseUrl={}, playwrightTimeoutMs={}", 
                  bindings.get("baseUrl"), bindings.get("playwrightTimeoutMs"));
        
        // 从上下文获取任务相关变量
        bindings.put("taskId", Optional.ofNullable(context).map(c -> c.get("taskId")).orElse(null));
        bindings.put("taskCode", Optional.ofNullable(context).map(c -> c.get("taskCode")).orElse(null));
        bindings.put("enterpriseName", Optional.ofNullable(context).map(c -> c.get("enterpriseName")).orElse(null));
        bindings.put("taxNo", Optional.ofNullable(context).map(c -> c.get("taxNo")).orElse(null));
        bindings.put("categoryId", Optional.ofNullable(context).map(c -> c.get("categoryId")).orElse(null));
        log.debug("注入任务变量: taskId={}, taskCode={}, enterpriseName={}, taxNo={}, categoryId={}", 
                  bindings.get("taskId"), bindings.get("taskCode"), bindings.get("enterpriseName"), 
                  bindings.get("taxNo"), bindings.get("categoryId"));
        
        // 注入Page对象（如果存在）
        String executionId = Optional.ofNullable(context).map(c -> c.get("executionId")).map(Object::toString).orElse(null);
        if (executionId != null) {
            Page page = getPage(executionId);
            bindings.put("page", page);
            log.debug("注入Page对象: {}", page != null ? "存在" : "不存在");
        }
        
        // 注入上一步的结果（如果存在）
        if (context != null) {
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                if (entry.getKey().startsWith("stepOutput_")) {
                    bindings.put(entry.getKey(), entry.getValue());
                    log.debug("注入上一步结果: {} = {}", entry.getKey(), entry.getValue());
                }
            }
        }
        
        return bindings;
    }
    
    /**
     * 获取环境变量，如果不存在则返回默认值
     * @param key 环境变量键
     * @param defaultValue 默认值
     * @return 环境变量值
     */
    private String getEnvironmentVariable(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    /**
     * 执行Java流程步骤（Groovy脚本）
     * @param step Java流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    @Override
    public ExecutionStep runJavaStep(ProcessStep step, Map<String, Object> context) {
        ExecutionStep executionStep = new ExecutionStep();
        executionStep.setStepName(step.getStepName());
        executionStep.setStepType(step.getStepType());
        executionStep.setExecuteTime(LocalDateTime.now());

        try {
            // 获取Groovy代码
            String code = step.getCodeContent();
            if (code == null || code.isEmpty()) {
                throw new RuntimeException("Groovy代码为空");
            }
            
            log.info("开始执行Groovy脚本: {}", step.getStepName());
            
            // 获取Groovy脚本引擎
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("groovy");
            if (engine == null) {
                throw new RuntimeException("未找到Groovy引擎，请确认已引入groovy-jsr223依赖");
            }
            
            // 注入变量
            SimpleBindings bindings = createBindings(context, step);
            
            // 执行脚本
            Object result = engine.eval(code, bindings);
            
            // 处理执行结果
            String output = result != null ? result.toString() : "null";
            executionStep.setOutput(output);
            log.info("Groovy脚本执行成功: {}, 结果: {}", step.getStepName(), output);
            return executionStep;
        } catch (Exception e) {
            String errorMsg = "Groovy脚本执行失败: " + e.getMessage();
            log.error(errorMsg, e);
            executionStep.setOutput("Error: " + e.getMessage());
        }
        return executionStep;
    }

    /**
     * 执行脚本流程步骤（JavaScript脚本）
     * @param step 脚本流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    @Override
    public ExecutionStep runScriptStep(ProcessStep step, Map<String, Object> context) {
        ExecutionStep executionStep = new ExecutionStep();
        executionStep.setStepName(step.getStepName());
        executionStep.setStepType(step.getStepType());
        executionStep.setExecuteTime(LocalDateTime.now());

        try {
            // 获取JavaScript代码
            String code = step.getCodeContent();
            if (code == null || code.isEmpty()) {
                throw new RuntimeException("JavaScript代码为空");
            }
            
            log.info("开始执行JavaScript脚本: {}", step.getStepName());
            
            // 获取JavaScript脚本引擎
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            if (engine == null) {
                throw new RuntimeException("未找到JavaScript引擎");
            }
            
            // 注入变量
            SimpleBindings bindings = createBindings(context, step);
            
            // 执行脚本
            Object result = engine.eval(code, bindings);
            
            // 处理执行结果
            String output = result != null ? result.toString() : "null";
            executionStep.setOutput(output);
            log.info("JavaScript脚本执行成功: {}, 结果: {}", step.getStepName(), output);
            return executionStep;
        } catch (Exception e) {
            String errorMsg = "JavaScript脚本执行失败: " + e.getMessage();
            log.error(errorMsg, e);
            executionStep.setOutput("Error: " + e.getMessage());
        }
        return executionStep;
    }

    /**
     * 创建失败的执行步骤
     * @param errorMsg 错误信息
     * @return 失败的执行步骤
     */
    private ExecutionStep createFailedExecutionStep(String errorMsg) {
        ExecutionStep executionStep = new ExecutionStep();
        executionStep.setStepName("未知步骤");
        executionStep.setStepType("未知类型");
        executionStep.setExecuteTime(LocalDateTime.now());
        executionStep.setOutput("Error: " + errorMsg);
        log.error("创建失败的执行步骤: {}", errorMsg);
        return executionStep;
    }
}
