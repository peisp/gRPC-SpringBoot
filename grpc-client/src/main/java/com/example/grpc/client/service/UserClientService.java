package com.example.grpc.client.service;

import com.example.grpc.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * gRPC客户端服务类
 * 
 * 这个类封装了对gRPC服务的调用
 * 使用@GrpcClient注解注入gRPC客户端stub
 * 
 * 演示了四种类型的RPC调用：
 * 1. 一元RPC
 * 2. 服务器流式RPC
 * 3. 客户端流式RPC
 * 4. 双向流式RPC
 */
@Service
@Slf4j
public class UserClientService {
    
    /**
     * 注入阻塞式客户端stub
     * 用于一元RPC调用，调用会阻塞直到收到响应
     */
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;
    
    /**
     * 注入异步客户端stub
     * 用于流式RPC调用，支持异步和流式通信
     */
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceStub asyncStub;
    
    /**
     * 创建用户 - 一元RPC调用
     * 
     * @param username 用户名
     * @param email 邮箱
     * @param age 年龄
     * @return 创建结果
     */
    public CreateUserResponse createUser(String username, String email, int age) {
        log.info("调用gRPC创建用户: username={}, email={}, age={}", username, email, age);
        
        // 构建请求
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setEmail(email)
                .setAge(age)
                .build();
        
        try {
            // 调用gRPC服务（阻塞调用）
            CreateUserResponse response = blockingStub.createUser(request);
            log.info("用户创建响应: success={}, message={}", response.getSuccess(), response.getMessage());
            return response;
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return CreateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("客户端调用失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 获取用户 - 一元RPC调用
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    public GetUserResponse getUser(long userId) {
        log.info("调用gRPC获取用户: userId={}", userId);
        
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUserId(userId)
                .build();
        
        try {
            GetUserResponse response = blockingStub.getUser(request);
            log.info("获取用户响应: success={}, message={}", response.getSuccess(), response.getMessage());
            return response;
        } catch (Exception e) {
            log.error("获取用户失败", e);
            return GetUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("客户端调用失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 获取所有用户 - 服务器流式RPC调用
     * 
     * 这个方法演示了如何处理服务器流式响应
     * 服务器会逐个发送用户数据
     * 
     * @return 所有用户列表
     */
    public List<User> getAllUsers() {
        log.info("调用gRPC获取所有用户（流式）");
        
        GetAllUsersRequest request = GetAllUsersRequest.newBuilder()
                .setPageSize(10)
                .build();
        
        List<User> users = new ArrayList<>();
        
        try {
            // 调用流式RPC，这会返回一个Iterator
            blockingStub.getAllUsers(request).forEachRemaining(user -> {
                log.info("收到用户: id={}, username={}", user.getId(), user.getUsername());
                users.add(user);
            });
            
            log.info("流式获取用户完成，共{}个用户", users.size());
            return users;
            
        } catch (Exception e) {
            log.error("获取所有用户失败", e);
            return users;
        }
    }
    
    /**
     * 更新用户 - 一元RPC调用
     * 
     * @param userId 用户ID
     * @param username 新用户名
     * @param email 新邮箱
     * @param age 新年龄
     * @return 更新结果
     */
    public UpdateUserResponse updateUser(long userId, String username, String email, int age) {
        log.info("调用gRPC更新用户: userId={}", userId);
        
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserId(userId)
                .setUsername(username)
                .setEmail(email)
                .setAge(age)
                .build();
        
        try {
            UpdateUserResponse response = blockingStub.updateUser(request);
            log.info("更新用户响应: success={}, message={}", response.getSuccess(), response.getMessage());
            return response;
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("客户端调用失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 删除用户 - 一元RPC调用
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    public DeleteUserResponse deleteUser(long userId) {
        log.info("调用gRPC删除用户: userId={}", userId);
        
        DeleteUserRequest request = DeleteUserRequest.newBuilder()
                .setUserId(userId)
                .build();
        
        try {
            DeleteUserResponse response = blockingStub.deleteUser(request);
            log.info("删除用户响应: success={}, message={}", response.getSuccess(), response.getMessage());
            return response;
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("客户端调用失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 批量创建用户 - 客户端流式RPC调用
     * 
     * 这个方法演示了客户端流式RPC
     * 客户端发送多个请求，服务器返回一个响应
     * 
     * @param userRequests 用户创建请求列表
     * @return 批量创建结果
     */
    public CreateUserResponse batchCreateUsers(List<CreateUserRequest> userRequests) {
        log.info("调用gRPC批量创建用户: 数量={}", userRequests.size());
        
        // 使用CountDownLatch等待异步调用完成
        CountDownLatch latch = new CountDownLatch(1);
        CreateUserResponse[] response = new CreateUserResponse[1];
        
        // 获取客户端流式观察者
        StreamObserver<CreateUserRequest> requestObserver = asyncStub.batchCreateUsers(
                new StreamObserver<CreateUserResponse>() {
                    @Override
                    public void onNext(CreateUserResponse value) {
                        // 收到服务器响应
                        log.info("批量创建响应: success={}, message={}", value.getSuccess(), value.getMessage());
                        response[0] = value;
                    }
                    
                    @Override
                    public void onError(Throwable t) {
                        log.error("批量创建失败", t);
                        response[0] = CreateUserResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("批量创建失败: " + t.getMessage())
                                .build();
                        latch.countDown();
                    }
                    
                    @Override
                    public void onCompleted() {
                        log.info("批量创建完成");
                        latch.countDown();
                    }
                }
        );
        
        try {
            // 发送所有请求
            for (CreateUserRequest request : userRequests) {
                log.info("发送用户创建请求: username={}", request.getUsername());
                requestObserver.onNext(request);
                
                // 模拟延迟
                Thread.sleep(100);
            }
            
            // 标识客户端发送完成
            requestObserver.onCompleted();
            
            // 等待服务器响应（最多等待30秒）
            if (latch.await(30, TimeUnit.SECONDS)) {
                return response[0];
            } else {
                log.error("批量创建超时");
                return CreateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("批量创建超时")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("批量创建异常", e);
            return CreateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("客户端异常: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 用户聊天 - 双向流式RPC调用
     * 
     * 这个方法演示了双向流式RPC
     * 客户端和服务器都可以发送多个消息
     * 
     * @param messages 要发送的消息列表
     * @return 收到的响应消息列表
     */
    public List<ChatMessage> userChat(List<String> messages) {
        log.info("开始gRPC聊天会话，消息数量: {}", messages.size());
        
        List<ChatMessage> responses = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        // 创建双向流
        StreamObserver<ChatMessage> requestObserver = asyncStub.userChat(
                new StreamObserver<ChatMessage>() {
                    @Override
                    public void onNext(ChatMessage message) {
                        // 收到服务器消息
                        log.info("收到聊天响应: userId={}, message={}", 
                                message.getUserId(), message.getMessage());
                        responses.add(message);
                    }
                    
                    @Override
                    public void onError(Throwable t) {
                        log.error("聊天过程中发生错误", t);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onCompleted() {
                        log.info("聊天会话结束");
                        latch.countDown();
                    }
                }
        );
        
        try {
            // 发送所有消息
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage message = ChatMessage.newBuilder()
                        .setUserId(1)  // 假设用户ID为1
                        .setMessage(messages.get(i))
                        .setTimestamp(System.currentTimeMillis())
                        .build();
                
                log.info("发送聊天消息: {}", messages.get(i));
                requestObserver.onNext(message);
                
                // 模拟延迟
                Thread.sleep(1000);
            }
            
            // 结束客户端流
            requestObserver.onCompleted();
            
            // 等待服务器响应完成
            if (latch.await(30, TimeUnit.SECONDS)) {
                log.info("聊天会话完成，收到{}条响应", responses.size());
                return responses;
            } else {
                log.error("聊天会话超时");
                return responses;
            }
            
        } catch (Exception e) {
            log.error("聊天异常", e);
            return responses;
        }
    }
} 