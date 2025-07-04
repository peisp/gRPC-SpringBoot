# API使用示例

本文档提供了详细的API使用示例，帮助你快速上手gRPC项目。

## 📋 目录

- [环境准备](#环境准备)
- [基础API测试](#基础api测试)
- [流式RPC示例](#流式rpc示例)
- [错误处理示例](#错误处理示例)
- [性能测试](#性能测试)
- [高级用法](#高级用法)

## 🔧 环境准备

### 1. 启动服务

确保服务端和客户端都已启动：

```bash
# 终端1: 启动服务端
cd grpc-server
mvn spring-boot:run

# 终端2: 启动客户端
cd grpc-client
mvn spring-boot:run
```

### 2. 验证服务状态

```bash
# 检查服务端健康状态
curl http://localhost:8080/health

# 检查客户端状态
curl http://localhost:8081/api/users
```

## 🚀 基础API测试

### 1. 创建用户 (一元RPC)

**请求示例:**
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "age": 28
  }'
```

**响应示例:**
```json
{
  "success": true,
  "message": "用户创建成功",
  "user": {
    "id": 1,
    "username": "alice",
    "email": "alice@example.com",
    "age": 28,
    "createdAt": 1699123456789
  }
}
```

**错误响应示例:**
```json
{
  "success": false,
  "message": "用户名已存在"
}
```

### 2. 获取单个用户

**请求示例:**
```bash
curl http://localhost:8081/api/users/1
```

**响应示例:**
```json
{
  "success": true,
  "message": "用户查询成功",
  "user": {
    "id": 1,
    "username": "alice",
    "email": "alice@example.com",
    "age": 28,
    "createdAt": 1699123456789
  }
}
```

### 3. 更新用户

**请求示例:**
```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_updated",
    "email": "alice.new@example.com",
    "age": 29
  }'
```

### 4. 删除用户

**请求示例:**
```bash
curl -X DELETE http://localhost:8081/api/users/1
```

**响应示例:**
```json
{
  "success": true,
  "message": "用户删除成功"
}
```

## 🌊 流式RPC示例

### 1. 获取所有用户 (服务器流式RPC)

这个API演示了服务器流式RPC，服务器会逐个返回用户数据。

**请求示例:**
```bash
curl http://localhost:8081/api/users
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取用户列表成功",
  "count": 3,
  "users": [
    {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "age": 28,
      "createdAt": 1699123456789
    },
    {
      "id": 2,
      "username": "bob",
      "email": "bob@example.com",
      "age": 25,
      "createdAt": 1699123457890
    },
    {
      "id": 3,
      "username": "charlie",
      "email": "charlie@example.com",
      "age": 30,
      "createdAt": 1699123458901
    }
  ]
}
```

### 2. 批量创建用户 (客户端流式RPC)

这个API演示了客户端流式RPC，客户端发送多个用户创建请求。

**请求示例:**
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
    },
    {
      "username": "user3",
      "email": "user3@example.com",
      "age": 22
    }
  ]'
```

**响应示例:**
```json
{
  "success": true,
  "message": "批量创建完成：成功3个，失败0个"
}
```

### 3. 用户聊天 (双向流式RPC)

这个API演示了双向流式RPC，模拟实时聊天功能。

**请求示例:**
```bash
curl -X POST http://localhost:8081/api/users/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      "Hello, server!",
      "How are you today?",
      "This is a streaming demo",
      "Goodbye!"
    ]
  }'
```

**响应示例:**
```json
{
  "success": true,
  "message": "聊天完成",
  "responses": [
    {
      "userId": 0,
      "message": "服务器收到你的消息: Hello, server!",
      "timestamp": 1699123460000
    },
    {
      "userId": 0,
      "message": "服务器收到你的消息: How are you today?",
      "timestamp": 1699123461000
    },
    {
      "userId": 0,
      "message": "服务器收到你的消息: This is a streaming demo",
      "timestamp": 1699123462000
    },
    {
      "userId": 0,
      "message": "服务器收到你的消息: Goodbye!",
      "timestamp": 1699123463000
    },
    {
      "userId": 0,
      "message": "聊天会话已结束，再见！",
      "timestamp": 1699123464000
    }
  ]
}
```

## ❌ 错误处理示例

### 1. 重复用户名错误

**请求:**
```bash
# 先创建一个用户
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "duplicate_user",
    "email": "user1@example.com",
    "age": 25
  }'

# 再次创建相同用户名的用户
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "duplicate_user",
    "email": "user2@example.com",
    "age": 26
  }'
```

**错误响应:**
```json
{
  "success": false,
  "message": "用户名已存在"
}
```

### 2. 用户不存在错误

**请求:**
```bash
curl http://localhost:8081/api/users/999
```

**错误响应:**
```json
{
  "success": false,
  "message": "用户不存在"
}
```

### 3. 参数验证错误

**请求:**
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "email": "",
    "age": 25
  }'
```

**错误响应:**
```json
{
  "success": false,
  "message": "用户名和邮箱不能为空"
}
```

## 🎯 完整测试流程

以下是一个完整的测试流程，演示了所有API的使用：

```bash
#!/bin/bash

echo "=== gRPC Demo API测试 ==="

echo "1. 创建用户Alice"
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "age": 28
  }'

echo -e "\n\n2. 创建用户Bob"
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "bob@example.com",
    "age": 25
  }'

echo -e "\n\n3. 获取用户Alice"
curl http://localhost:8081/api/users/1

echo -e "\n\n4. 获取所有用户（流式RPC）"
curl http://localhost:8081/api/users

echo -e "\n\n5. 更新用户Alice"
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_updated",
    "email": "alice.new@example.com",
    "age": 29
  }'

echo -e "\n\n6. 批量创建用户（客户端流式RPC）"
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

echo -e "\n\n7. 用户聊天（双向流式RPC）"
curl -X POST http://localhost:8081/api/users/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": ["Hello", "How are you?", "Goodbye"]
  }'

echo -e "\n\n8. 删除用户"
curl -X DELETE http://localhost:8081/api/users/2

echo -e "\n\n9. 查看最终用户列表"
curl http://localhost:8081/api/users

echo -e "\n\n=== 测试完成 ==="
```

## 📊 性能测试

### 使用Apache Bench进行简单压力测试

```bash
# 创建用户的并发测试
echo '{"username":"test_user","email":"test@example.com","age":25}' > test_user.json

ab -n 100 -c 10 -p test_user.json -T application/json \
   http://localhost:8081/api/users

# 获取用户的并发测试
ab -n 1000 -c 50 http://localhost:8081/api/users/1
```

### 使用wrk进行高级性能测试

```bash
# 安装wrk (macOS)
brew install wrk

# 创建用户测试脚本
cat > create_user.lua << 'EOF'
wrk.method = "POST"
wrk.body = '{"username":"perf_user","email":"perf@example.com","age":25}'
wrk.headers["Content-Type"] = "application/json"
EOF

# 运行性能测试
wrk -t4 -c100 -d30s -s create_user.lua http://localhost:8081/api/users
```

## 🔧 使用grpcurl直接测试gRPC服务

如果你安装了grpcurl，可以直接测试gRPC服务：

### 安装grpcurl

```bash
# macOS
brew install grpcurl

# 或从GitHub下载
# https://github.com/fullstorydev/grpcurl/releases
```

### 基本命令

```bash
# 列出所有服务
grpcurl -plaintext localhost:9090 list

# 列出UserService的所有方法
grpcurl -plaintext localhost:9090 list com.example.grpc.proto.UserService

# 查看CreateUser方法的详细信息
grpcurl -plaintext localhost:9090 describe com.example.grpc.proto.UserService.CreateUser
```

### gRPC调用示例

```bash
# 创建用户
grpcurl -plaintext -d '{
  "username": "grpc_user",
  "email": "grpc@example.com",
  "age": 30
}' localhost:9090 com.example.grpc.proto.UserService/CreateUser

# 获取用户
grpcurl -plaintext -d '{"user_id": 1}' \
  localhost:9090 com.example.grpc.proto.UserService/GetUser

# 获取所有用户（流式响应）
grpcurl -plaintext -d '{"page_size": 10}' \
  localhost:9090 com.example.grpc.proto.UserService/GetAllUsers

# 更新用户
grpcurl -plaintext -d '{
  "user_id": 1,
  "username": "updated_user",
  "email": "updated@example.com",
  "age": 31
}' localhost:9090 com.example.grpc.proto.UserService/UpdateUser

# 删除用户
grpcurl -plaintext -d '{"user_id": 1}' \
  localhost:9090 com.example.grpc.proto.UserService/DeleteUser
```

## 📝 日志分析

在测试过程中，注意观察服务端和客户端的日志输出，这将帮助你理解gRPC的调用流程：

### 服务端日志示例
```
2023-11-05 10:30:15 [grpc-default-executor-0] INFO  c.e.g.s.s.UserGrpcService - 收到创建用户请求: username=alice, email=alice@example.com, age=28
2023-11-05 10:30:15 [grpc-default-executor-0] INFO  c.e.g.s.s.UserGrpcService - 用户创建成功: id=1
```

### 客户端日志示例
```
2023-11-05 10:30:15 [http-nio-8081-exec-1] INFO  c.e.g.c.s.UserClientService - 调用gRPC创建用户: username=alice, email=alice@example.com, age=28
2023-11-05 10:30:15 [http-nio-8081-exec-1] INFO  c.e.g.c.s.UserClientService - 用户创建响应: success=true, message=用户创建成功
```

## 🎉 总结

通过这些API示例，你可以：

1. **理解四种RPC类型**：一元、服务器流式、客户端流式、双向流式
2. **掌握错误处理**：学习如何处理各种异常情况
3. **性能测试**：了解gRPC的性能特征
4. **调试技巧**：使用grpcurl等工具进行调试

继续探索和实验这些API，你将对gRPC有更深入的理解！ 