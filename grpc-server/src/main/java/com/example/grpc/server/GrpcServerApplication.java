package com.example.grpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * gRPC服务端应用程序启动类
 * 
 * 这是一个标准的Spring Boot应用程序，通过@SpringBootApplication注解
 * 启用了自动配置、组件扫描和配置属性扫描
 * 
 * gRPC服务会通过grpc-spring-boot-starter自动启动
 * 
 * @author 示例作者
 */
@SpringBootApplication
public class GrpcServerApplication {
    
    public static void main(String[] args) {
        // 启动Spring Boot应用程序
        SpringApplication.run(GrpcServerApplication.class, args);
        System.out.println("gRPC服务端启动成功！");
        System.out.println("gRPC端口: 9090");
        System.out.println("HTTP端口: 8080");
    }
} 