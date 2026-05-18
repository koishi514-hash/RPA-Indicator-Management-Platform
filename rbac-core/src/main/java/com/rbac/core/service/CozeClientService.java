package com.rbac.core.service;

import com.alibaba.fastjson2.JSON;
import com.rbac.common.config.CozeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CozeClientService {

    private final CozeProperties cozeProperties;

    public Map<String, Object> chat(String query) {
        try {
            String url = "https://api.coze.cn/open_api/v2/chat";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cozeProperties.getApiKey());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("bot_id", cozeProperties.getBotId());
            requestBody.put("user", cozeProperties.getUserId());
            requestBody.put("query", query);
            requestBody.put("stream", false);

            String jsonBody = JSON.toJSONString(requestBody);
            log.info("发送给 Coze 的完整 JSON: {}", jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("调用 Coze API, url: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            log.info("Coze API 响应, status: {}, body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseMap = JSON.parseObject(response.getBody(), Map.class);
                
                // 检查 Coze 返回的错误码
                if (responseMap != null && responseMap.containsKey("code")) {
                    Object codeObj = responseMap.get("code");
                    if (codeObj instanceof Number) {
                        int code = ((Number) codeObj).intValue();
                        if (code == 4028) {
                            log.error("Coze API 返回错误码 4028: 积分不足");
                            throw new CozeInsufficientCreditsException();
                        } else if (code != 0) {
                            // 其他错误码
                            log.error("Coze API 返回错误码: {}, 消息: {}", code, responseMap.get("msg"));
                            throw new CozeApiException("Coze API 错误: " + responseMap.get("msg"));
                        }
                    }
                }
                
                return responseMap;
            } else {
                throw new RuntimeException("Coze API 调用失败: " + response.getStatusCode());
            }
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Coze API 调用超时或连接失败", e);
            // 检查是否是超时异常
            if (e.getCause() != null && (e.getCause() instanceof java.net.SocketTimeoutException 
                    || e.getCause().getMessage() != null && e.getCause().getMessage().contains("timeout"))) {
                throw new CozeTimeoutException();
            }
            throw new RuntimeException("调用 Coze API 失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("调用 Coze API 失败", e);
            if (e instanceof CozeInsufficientCreditsException || e instanceof CozeTimeoutException || e instanceof CozeApiException) {
                throw e;
            }
            throw new RuntimeException("调用 Coze API 失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> calculateQuota(String indicatorCodes, String conditions,
                                              String calculation, String resultVarName,
                                              String outputTemplate, Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("indicatorCodes", indicatorCodes);
        payload.put("conditions", conditions);
        payload.put("calculation", calculation);
        payload.put("resultVarName", resultVarName);
        payload.put("outputTemplate", outputTemplate);
        payload.put("businessData", data);

        String query = JSON.toJSONString(payload);

        Map<String, Object> response = chat(query);

        try {
            String content = null;
            if (response != null && response.containsKey("messages")) {
                Object messagesObj = response.get("messages");
                if (messagesObj instanceof List) {
                    List<?> messages = (List<?>) messagesObj;
                    for (Object msg : messages) {
                        if (msg instanceof Map) {
                            Map<?, ?> msgMap = (Map<?, ?>) msg;
                            Object role = msgMap.get("role");
                            Object type = msgMap.get("type");
                            if ("assistant".equals(role) && "answer".equals(type)) {
                                content = (String) msgMap.get("content");
                                break;
                            }
                        }
                    }
                }
            }

            if (content != null) {
                return JSON.parseObject(content, Map.class);
            }

            log.warn("未找到有效的响应内容");
            Map<String, Object> result = new HashMap<>();
            result.put("rawResponse", response);
            return result;
        } catch (Exception e) {
            log.warn("解析 Coze 响应失败，返回原始响应", e);
            Map<String, Object> result = new HashMap<>();
            result.put("rawResponse", response);
            return result;
        }
    }

    public static class CozeInsufficientCreditsException extends RuntimeException {
        public CozeInsufficientCreditsException() {
            super("智能体出现错误，请联系管理员");
        }
    }

    public static class CozeTimeoutException extends RuntimeException {
        public CozeTimeoutException() {
            super("计算失败，请稍后重试");
        }
    }

    public static class CozeApiException extends RuntimeException {
        public CozeApiException(String message) {
            super(message);
        }
    }
}
