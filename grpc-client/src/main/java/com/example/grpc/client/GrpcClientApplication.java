package com.example.grpc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * gRPC客户端应用程序启动类
 * 
 * 这个客户端应用程序提供REST API接口，内部调用gRPC服务
 * 演示了如何将gRPC服务包装成HTTP API
 * 
 * @author 示例作者
 */
@SpringBootApplication
public class GrpcClientApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
        System.out.println("gRPC客户端启动成功！");
        System.out.println("HTTP端口: 8081");
        System.out.println("可以通过HTTP API调用gRPC服务");
    }
} 