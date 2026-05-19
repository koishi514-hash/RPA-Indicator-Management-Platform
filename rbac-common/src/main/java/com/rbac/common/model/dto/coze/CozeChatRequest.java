package com.rbac.common.model.dto.coze;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Coze 聊天请求 DTO
 * Coze API 兼容 OpenAI 格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CozeChatRequest {

    /**
     * 模型名称
     */
    private String model = "gpt-4o";

    /**
     * 聊天消息列表
     */
    private List<Message> messages;

    /**
     * 温度参数 (0-2)
     */
    private Double temperature = 0.7;

    /**
     * 最大输出token数
     */
    private Integer maxTokens = 4096;

    /**
     * 是否流式输出
     */
    private Boolean stream = false;

    /**
     * 工具调用配置
     */
    private List<Map<String, Object>> tools;

    /**
     * 工具调用方式
     */
    private String toolChoice = "auto";

    /**
     * 消息类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 角色：system, user, assistant, tool
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;

        /**
         * 工具调用结果（当role为tool时使用）
         */
        private Map<String, Object> toolCall;
    }
}
