# gRPC Spring Boot Demo

一个完整的gRPC学习项目，使用Java和Spring Boot构建，包含服务端和客户端示例，涵盖四种RPC类型的完整演示。

## 📚 项目简介

这个项目是专门为学习gRPC而设计的完整示例，通过一个用户管理系统演示了gRPC的核心概念和四种不同类型的RPC调用：

- **一元RPC** (Unary RPC): 客户端发送单个请求，服务器返回单个响应
- **服务器流式RPC** (Server Streaming RPC): 客户端发送单个请求，服务器返回流式响应
- **客户端流式RPC** (Client Streaming RPC): 客户端发送流式请求，服务器返回单个响应  
- **双向流式RPC** (Bidirectional Streaming RPC): 客户端和服务器都发送流式数据

## 🔧 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **gRPC 1.59.0**
- **Protocol Buffers 3.24.4**
- **Spring Data JPA**
- **H2 Database** (内存数据库)
- **Maven** (项目管理)
- **Lombok** (简化代码)

## 📁 项目结构

```
gRPC-test/
├── pom.xml                                 # 根项目Maven配置
├── README.md                               # 项目说明文档
│
├── grpc-proto/                             # Protocol Buffers定义模块
│   ├── pom.xml                            # Proto模块Maven配置
│   └── src/main/proto/
│       └── user_service.proto             # gRPC服务定义文件
│
├── grpc-server/                           # gRPC服务端模块
│   ├── pom.xml                            # 服务端Maven配置
│   └── src/main/
│       ├── java/com/example/grpc/server/
│       │   ├── GrpcServerApplication.java # 服务端启动类
│       │   ├── entity/UserEntity.java    # 用户实体类
│       │   ├── repository/UserRepository.java # 数据访问层
│       │   ├── service/UserGrpcService.java   # gRPC服务实现
│       │   └── controller/HealthController.java # HTTP健康检查
│       └── resources/
│           └── application.yml            # 服务端配置文件
│
└── grpc-client/                           # gRPC客户端模块
    ├── pom.xml                            # 客户端Maven配置
    └── src/main/
        ├── java/com/example/grpc/client/
        │   ├── GrpcClientApplication.java # 客户端启动类
        │   ├── service/UserClientService.java # gRPC客户端服务
        │   └── controller/UserController.java # REST API控制器
        └── resources/
            └── application.yml            # 客户端配置文件
```

## 🚀 快速开始

### 前置条件

- JDK 17或更高版本
- Maven 3.6或更高版本

### 1. 克隆项目

```bash
git clone <项目地址>
cd gRPC-test
```

### 2. 编译项目

```bash
# 编译整个项目（包括生成gRPC代码）
mvn clean compile

# 或者只编译proto模块生成gRPC代码
cd grpc-proto
mvn clean compile
cd ..
```

### 3. 启动服务端

```bash
# 启动gRPC服务端
cd grpc-server
mvn spring-boot:run
```

服务端启动后会在以下端口提供服务：
- **gRPC端口**: 9090
- **HTTP端口**: 8080

### 4. 启动客户端

新开一个终端窗口：

```bash
# 启动gRPC客户端（REST API服务器）
cd grpc-client
mvn spring-boot:run
```

客户端启动后会在以下端口提供服务：
- **HTTP端口**: 8081

### 5. 验证服务

访问健康检查接口：
- 服务端: http://localhost:8080/health
- 客户端: http://localhost:8081/actuator/health（如果配置了actuator）

## 📖 gRPC基础概念

### 什么是gRPC？

gRPC (gRPC Remote Procedure Calls) 是由Google开发的高性能、开源的RPC框架。它使用Protocol Buffers作为接口定义语言和消息序列化格式。

### gRPC的优势

1. **高性能**: 使用HTTP/2协议，支持多路复用、流控制等特性
2. **跨语言**: 支持多种编程语言
3. **强类型**: 使用Protocol Buffers定义接口，类型安全
4. **流式支持**: 原生支持流式数据传输
5. **负载均衡**: 内置负载均衡支持

### 四种RPC类型详解

#### 1. 一元RPC (Unary RPC)
```protobuf
rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
```
- 最简单的RPC类型
- 客户端发送一个请求，服务器返回一个响应
- 类似于传统的HTTP请求/响应

#### 2. 服务器流式RPC (Server Streaming RPC)
```protobuf
rpc GetAllUsers(GetAllUsersRequest) returns (stream User);
```
- 客户端发送一个请求，服务器返回多个响应（流）
- 适用于返回大量数据的场景
- 服务器可以逐个发送数据，客户端逐个接收

#### 3. 客户端流式RPC (Client Streaming RPC)
```protobuf
rpc BatchCreateUsers(stream CreateUserRequest) returns (CreateUserResponse);
```
- 客户端发送多个请求（流），服务器返回一个响应
- 适用于批量上传数据的场景
- 客户端可以分批发送数据，服务器在接收完所有数据后统一处理

#### 4. 双向流式RPC (Bidirectional Streaming RPC)
```protobuf
rpc UserChat(stream ChatMessage) returns (stream ChatMessage);
```
- 客户端和服务器都可以发送多个消息
- 适用于实时通信场景
- 两个流可以独立操作，支持全双工通信

## 🎯 API使用示例

### 通过HTTP API调用gRPC服务

客户端提供了REST API接口，内部调用gRPC服务：

#### 1. 创建用户 (一元RPC)

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "age": 25
  }'
```

#### 2. 获取用户 (一元RPC)

```bash
curl http://localhost:8081/api/users/1
```

#### 3. 获取所有用户 (服务器流式RPC)

```bash
curl http://localhost:8081/api/users
```

#### 4. 更新用户 (一元RPC)

```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_updated",
    "email": "john.updated@example.com",
    "age": 26
  }'
```

#### 5. 删除用户 (一元RPC)

```bash
curl -X DELETE http://localhost:8081/api/users/1
```

#### 6. 批量创建用户 (客户端流式RPC)

```bash
curl -X POST http://localhost:8081/api/users/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "username": "user1",
      "email": "user1@example.com",
      "age": 20
    },
    {
      "username": "user2", 
      "email": "user2@example.com",
      "age": 21
    }
  ]'
```

#### 7. 用户聊天 (双向流式RPC)

```bash
curl -X POST http://localhost:8081/api/users/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": ["Hello", "How are you?", "Goodbye"]
  }'
```

### 直接使用grpcurl工具测试

如果你安装了grpcurl工具，可以直接测试gRPC服务：

```bash
# 获取服务列表
grpcurl -plaintext localhost:9090 list

# 创建用户
grpcurl -plaintext -d '{
  "username": "grpcurl_user",
  "email": "grpcurl@example.com", 
  "age": 30
}' localhost:9090 com.example.grpc.proto.UserService/CreateUser

# 获取用户
grpcurl -plaintext -d '{"user_id": 1}' \
  localhost:9090 com.example.grpc.proto.UserService/GetUser
```

## 💡 核心技术要点

### 1. Protocol Buffers定义

`user_service.proto`文件定义了：
- 消息类型 (Message Types): User, CreateUserRequest等
- 服务接口 (Service Interface): UserService
- RPC方法 (RPC Methods): CreateUser, GetUser等

### 2. 代码生成

通过Maven插件自动生成Java代码：
- **消息类**: UserOuterClass, CreateUserRequest等
- **服务接口**: UserServiceGrpc
- **Stub类**: UserServiceBlockingStub, UserServiceStub

### 3. 服务端实现

`UserGrpcService`类继承`UserServiceImplBase`，实现所有RPC方法：
- 使用`@GrpcService`注解标识gRPC服务
- 使用`StreamObserver`处理响应
- 支持同步和异步处理

### 4. 客户端调用

`UserClientService`类封装gRPC客户端调用：
- 使用`@GrpcClient`注解注入客户端stub
- 支持阻塞调用和异步调用
- 处理流式数据传输

### 5. Spring Boot集成

通过`grpc-spring-boot-starter`实现与Spring Boot的无缝集成：
- 自动配置gRPC服务器和客户端
- 支持Spring的依赖注入和AOP
- 提供健康检查和监控支持

### 6. 错误处理

项目演示了完整的错误处理机制：
- 服务端异常处理
- 客户端超时处理  
- 网络异常恢复

## 🔍 关键代码解析

### Proto文件结构

```protobuf
syntax = "proto3";                    // 使用proto3语法

package com.example.grpc.proto;       // 包名定义

option java_package = "com.example.grpc.proto";      // Java包选项
option java_outer_classname = "UserServiceProto";    // 外部类名
option java_multiple_files = true;                   // 生成多个文件

message User {                        // 消息定义
  int64 id = 1;                      // 字段编号从1开始
  string username = 2;
  string email = 3;
  int32 age = 4;
  int64 created_at = 5;
}

service UserService {                 // 服务定义
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
  rpc GetAllUsers(GetAllUsersRequest) returns (stream User);
}
```

### 服务端核心代码

```java
@GrpcService  // 标识gRPC服务
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    @Override
    public void createUser(CreateUserRequest request, 
                          StreamObserver<CreateUserResponse> responseObserver) {
        try {
            // 1. 业务逻辑处理
            UserEntity user = processCreateUser(request);
            
            // 2. 构建响应
            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setSuccess(true)
                    .setUser(convertToProto(user))
                    .build();
            
            // 3. 发送响应
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            // 4. 错误处理
            responseObserver.onError(e);
        }
    }
}
```

### 客户端核心代码

```java
@Service
public class UserClientService {
    
    @GrpcClient("user-service")  // 注入gRPC客户端
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;
    
    public CreateUserResponse createUser(String username, String email, int age) {
        // 1. 构建请求
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setEmail(email)
                .setAge(age)
                .build();
        
        // 2. 调用gRPC服务
        return blockingStub.createUser(request);
    }
}
```

## 🛠️ 开发和调试技巧

### 1. 使用H2控制台查看数据

访问 http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:grpc_demo`
- Username: `sa`
- Password: (空)

### 2. 启用gRPC反射服务

服务端已启用反射服务，可以使用grpcurl等工具进行调试：

```bash
# 查看所有服务
grpcurl -plaintext localhost:9090 list

# 查看服务方法
grpcurl -plaintext localhost:9090 list com.example.grpc.proto.UserService

# 查看方法详情
grpcurl -plaintext localhost:9090 describe com.example.grpc.proto.UserService.CreateUser
```

### 3. 日志配置

项目配置了详细的日志输出，帮助理解gRPC调用流程：
- gRPC调用日志
- SQL执行日志
- 业务逻辑日志

### 4. 性能监控

可以通过以下方式监控gRPC性能：
- Spring Boot Actuator
- gRPC内置指标
- 自定义监控点

## 🚧 扩展和改进建议

### 1. 安全增强
- 添加TLS/SSL支持
- 实现身份认证和授权
- 添加API限流

### 2. 生产环境配置
- 使用外部数据库 (MySQL, PostgreSQL)
- 添加连接池配置
- 配置服务发现

### 3. 监控和日志
- 集成Prometheus监控
- 添加分布式链路追踪
- 结构化日志输出

### 4. 测试完善
- 单元测试
- 集成测试
- gRPC服务测试

## 📚 学习资源

### 官方文档
- [gRPC官方网站](https://grpc.io/)
- [Protocol Buffers文档](https://developers.google.com/protocol-buffers)
- [Spring Boot gRPC Starter](https://github.com/yidongnan/grpc-spring-boot-starter)

### 推荐阅读
- gRPC设计原理和最佳实践
- Protocol Buffers编码原理
- HTTP/2协议详解
- 微服务架构中的RPC选型

## ❓ 常见问题

### Q: 如何处理gRPC连接失败？
A: 检查服务端是否启动，端口是否被占用，防火墙设置等。

### Q: 为什么选择H2数据库？
A: H2是内存数据库，方便演示，无需额外安装。生产环境建议使用MySQL或PostgreSQL。

### Q: 如何自定义gRPC配置？
A: 修改application.yml中的grpc配置项，或实现自定义配置类。

### Q: 流式RPC什么时候使用？
A: 
- 服务器流式：返回大量数据时
- 客户端流式：批量上传数据时  
- 双向流式：实时通信场景

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 📄 许可证

本项目使用MIT许可证，详见LICENSE文件。

---

**Happy Learning! 🎉**

通过这个项目，你将全面了解gRPC的核心概念和实际应用。如果有任何问题，欢迎创建Issue进行讨论！ 