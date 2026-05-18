package com.rbac.common.model.dto.coze;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Coze 聊天响应 DTO
 * Coze API 兼容 OpenAI 格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CozeChatResponse {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 响应ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 响应选择列表
     */
    private List<Choice> choices;

    /**
     * 用法统计
     */
    private Usage usage;

    /**
     * 选择项类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        /**
         * 消息
         */
        private Message message;

        /**
         * 结束原因
         */
        private String finishReason;

        /**
         * 索引
         */
        private Integer index;
    }

    /**
     * 消息类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;

        /**
         * 工具调用
         */
        private Map<String, Object> toolCall;
    }

    /**
     * 用法统计类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 提示词token数
         */
        private Integer promptTokens;

        /**
         * 完成token数
         */
        private Integer completionTokens;

        /**
         * 总token数
         */
        private Integer totalTokens;
    }
}
