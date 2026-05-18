package com.rbac.core.service;

import java.util.Map;

import com.rbac.core.domain.entity.ExecutionStep;
import com.rbac.core.domain.entity.ProcessStep;

public interface ProcessStepExecutor {
    
    /**
     * 执行流程步骤
     * @param step 流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    ExecutionStep executeStep(ProcessStep step, Map<String, Object> context);

    /**
     * 执行Java流程步骤
     * @param step Java流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    ExecutionStep runJavaStep(ProcessStep step, Map<String, Object> context);

    /**
     * 执行脚本流程步骤
     * @param step 脚本流程步骤
     * @param context 执行上下文
     * @return 执行结果
     */
    ExecutionStep runScriptStep(ProcessStep step, Map<String, Object> context);
}
