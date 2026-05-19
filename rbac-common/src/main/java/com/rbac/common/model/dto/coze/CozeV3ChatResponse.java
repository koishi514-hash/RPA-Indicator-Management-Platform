package com.rbac.common.model.dto.coze;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CozeV3ChatResponse {
    private Integer code;
    private String msg;
    private Data data;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String id;
        private String conversationId;
        private String botId;
        private String status;
        private List<Message> messages;
        private Long createdAt;
        private Long completedAt;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String type;
        private String content;
        private String contentType;
        private Map<String, Object> metadata;
    }
}
