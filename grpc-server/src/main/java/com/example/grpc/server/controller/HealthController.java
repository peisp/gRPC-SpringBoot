package com.example.grpc.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP健康检查控制器
 * 
 * 提供HTTP接口用于监控服务状态
 * 虽然主要使用gRPC通信，但HTTP接口对于监控和健康检查很有用
 */
@RestController
public class HealthController {
    
    /**
     * 健康检查接口
     * 
     * @return 服务状态信息
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "gRPC User Service");
        health.put("timestamp", System.currentTimeMillis());
        health.put("grpc_port", 9090);
        health.put("http_port", 8080);
        return health;
    }
    
    /**
     * 服务信息接口
     * 
     * @return 服务详细信息
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "gRPC Spring Boot Demo Server");
        info.put("description", "使用Spring Boot构建的gRPC服务端示例");
        info.put("version", "1.0.0");
        info.put("protocols", new String[]{"gRPC", "HTTP"});
        info.put("features", new String[]{
            "用户管理 (CRUD)",
            "流式RPC示例",
            "双向流通信",
            "批量操作"
        });
        return info;
    }
} 