package com.rbac.api.controller;

import com.rbac.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */

@Tag(name = "系统监控", description = "系统健康检查和监控接口")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("application", "Rbac System");
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    /**
     * 系统信息
     */
    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "金融级项目脚手架");
        data.put("version", "1.0.0");
        data.put("description", "基于 Spring Boot 3.x + MySQL 的金融级项目脚手架");
        data.put("javaVersion", System.getProperty("java.version"));
        data.put("osName", System.getProperty("os.name"));
        data.put("osVersion", System.getProperty("os.version"));
        return Result.success(data);
    }
}
