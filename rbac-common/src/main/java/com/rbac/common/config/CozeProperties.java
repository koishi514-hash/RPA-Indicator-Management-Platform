package com.rbac.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "coze")
public class CozeProperties {

    private String baseUrl = "https://api.coze.cn/v3";

    private String apiKey;

    private String botId;

    private String userId;

    private Integer connectionTimeout = 30000;

    private Integer readTimeout = 60000;
}
