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
public class CozeV3ChatRequest {
    private String botId;
    private String user;
    private String query;
    private Boolean stream;
    private String conversationId;
    private List<AdditionalMessage> additionalMessages;
    private Map<String, Object> customVariables;
    private Map<String, Object> parameters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalMessage {
        private String role;
        private String content;
        private String type;
    }
}
