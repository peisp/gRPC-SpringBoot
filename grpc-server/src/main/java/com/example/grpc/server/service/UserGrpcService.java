package com.example.grpc.server.service;

import com.example.grpc.proto.*;
import com.example.grpc.server.entity.UserEntity;
import com.example.grpc.server.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * gRPC用户服务实现类
 * 
 * @GrpcService注解标识这是一个gRPC服务实现
 * 继承UserServiceGrpc.UserServiceImplBase（由proto文件生成）
 * 
 * 这个类实现了.proto文件中定义的所有RPC方法
 */
@GrpcService  // 标识这是一个gRPC服务
@RequiredArgsConstructor  // Lombok注解：为final字段生成构造函数
@Slf4j  // Lombok注解：自动生成日志对象
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserRepository userRepository;
    
    /**
     * 创建用户 - 一元RPC
     * 
     * 这是最简单的RPC类型：客户端发送一个请求，服务器返回一个响应
     * 
     * @param request 创建用户请求
     * @param responseObserver 响应观察者（用于发送响应）
     */
    @Override
    @Transactional  // 数据库事务
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("收到创建用户请求: username={}, email={}, age={}", 
                request.getUsername(), request.getEmail(), request.getAge());
        
        try {
            // 1. 参数验证
            if (request.getUsername().isEmpty() || request.getEmail().isEmpty()) {
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("用户名和邮箱不能为空")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // 2. 检查用户名和邮箱是否已存在
            if (userRepository.existsByUsername(request.getUsername())) {
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("用户名已存在")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            if (userRepository.existsByEmail(request.getEmail())) {
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("邮箱已存在")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // 3. 创建用户实体
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(request.getUsername());
            userEntity.setEmail(request.getEmail());
            userEntity.setAge(request.getAge());
            
            // 4. 保存到数据库
            UserEntity savedUser = userRepository.save(userEntity);
            log.info("用户创建成功: id={}", savedUser.getId());
            
            // 5. 构建响应
            User user = convertToProtoUser(savedUser);
            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("用户创建成功")
                    .setUser(user)
                    .build();
            
            // 6. 发送响应
            responseObserver.onNext(response);
            responseObserver.onCompleted();  // 标识响应完成
            
        } catch (Exception e) {
            log.error("创建用户失败", e);
            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("服务器内部错误: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * 获取用户 - 一元RPC
     * 
     * @param request 获取用户请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        log.info("收到获取用户请求: userId={}", request.getUserId());
        
        try {
            Optional<UserEntity> userOpt = userRepository.findById(request.getUserId());
            
            if (userOpt.isPresent()) {
                User user = convertToProtoUser(userOpt.get());
                GetUserResponse response = GetUserResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("用户查询成功")
                        .setUser(user)
                        .build();
                responseObserver.onNext(response);
            } else {
                GetUserResponse response = GetUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("用户不存在")
                        .build();
                responseObserver.onNext(response);
            }
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("获取用户失败", e);
            GetUserResponse response = GetUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("服务器内部错误: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * 获取所有用户 - 服务器流式RPC
     * 
     * 这种RPC类型：客户端发送一个请求，服务器返回多个响应（流）
     * 适用于返回大量数据的场景
     * 
     * @param request 获取所有用户请求
     * @param responseObserver 响应观察者（可以多次调用onNext）
     */
    @Override
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<User> responseObserver) {
        log.info("收到获取所有用户请求: pageSize={}", request.getPageSize());
        
        try {
            List<UserEntity> users = userRepository.findAll();
            log.info("查询到{}个用户", users.size());
            
            // 流式发送每个用户
            for (UserEntity userEntity : users) {
                User user = convertToProtoUser(userEntity);
                responseObserver.onNext(user);  // 发送一个用户
                
                // 模拟延迟，演示流式响应效果
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            responseObserver.onCompleted();  // 标识流结束
            
        } catch (Exception e) {
            log.error("获取所有用户失败", e);
            responseObserver.onError(e);  // 发送错误
        }
    }
    
    /**
     * 更新用户 - 一元RPC
     */
    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        log.info("收到更新用户请求: userId={}", request.getUserId());
        
        try {
            Optional<UserEntity> userOpt = userRepository.findById(request.getUserId());
            
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                
                // 更新字段
                if (!request.getUsername().isEmpty()) {
                    // 检查新用户名是否已被其他用户使用
                    Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
                    if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                        UpdateUserResponse response = UpdateUserResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("用户名已被其他用户使用")
                                .build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                        return;
                    }
                    user.setUsername(request.getUsername());
                }
                
                if (!request.getEmail().isEmpty()) {
                    // 检查新邮箱是否已被其他用户使用
                    Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());
                    if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                        UpdateUserResponse response = UpdateUserResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("邮箱已被其他用户使用")
                                .build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                        return;
                    }
                    user.setEmail(request.getEmail());
                }
                
                if (request.getAge() > 0) {
                    user.setAge(request.getAge());
                }
                
                UserEntity savedUser = userRepository.save(user);
                User protoUser = convertToProtoUser(savedUser);
                
                UpdateUserResponse response = UpdateUserResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("用户更新成功")
                        .setUser(protoUser)
                        .build();
                responseObserver.onNext(response);
                
            } else {
                UpdateUserResponse response = UpdateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("用户不存在")
                        .build();
                responseObserver.onNext(response);
            }
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("更新用户失败", e);
            UpdateUserResponse response = UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("服务器内部错误: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * 删除用户 - 一元RPC
     */
    @Override
    @Transactional
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        log.info("收到删除用户请求: userId={}", request.getUserId());
        
        try {
            if (userRepository.existsById(request.getUserId())) {
                userRepository.deleteById(request.getUserId());
                
                DeleteUserResponse response = DeleteUserResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("用户删除成功")
                        .build();
                responseObserver.onNext(response);
            } else {
                DeleteUserResponse response = DeleteUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("用户不存在")
                        .build();
                responseObserver.onNext(response);
            }
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("删除用户失败", e);
            DeleteUserResponse response = DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("服务器内部错误: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * 批量创建用户 - 客户端流式RPC
     * 
     * 这种RPC类型：客户端发送多个请求（流），服务器返回一个响应
     * 适用于批量上传数据的场景
     * 
     * @param responseObserver 响应观察者
     * @return 请求观察者（用于接收客户端的流式请求）
     */
    @Override
    public StreamObserver<CreateUserRequest> batchCreateUsers(StreamObserver<CreateUserResponse> responseObserver) {
        log.info("开始批量创建用户");
        
        return new StreamObserver<CreateUserRequest>() {
            private final AtomicInteger successCount = new AtomicInteger(0);
            private final AtomicInteger failureCount = new AtomicInteger(0);
            
            @Override
            public void onNext(CreateUserRequest request) {
                // 每次收到一个用户创建请求
                log.info("收到批量创建用户请求: username={}", request.getUsername());
                
                try {
                    // 检查用户名和邮箱是否已存在
                    if (userRepository.existsByUsername(request.getUsername()) || 
                        userRepository.existsByEmail(request.getEmail())) {
                        failureCount.incrementAndGet();
                        log.warn("用户创建失败，用户名或邮箱已存在: {}", request.getUsername());
                        return;
                    }
                    
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUsername(request.getUsername());
                    userEntity.setEmail(request.getEmail());
                    userEntity.setAge(request.getAge());
                    
                    userRepository.save(userEntity);
                    successCount.incrementAndGet();
                    log.info("用户创建成功: {}", request.getUsername());
                    
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("批量创建用户失败: " + request.getUsername(), e);
                }
            }
            
            @Override
            public void onError(Throwable t) {
                log.error("批量创建用户过程中发生错误", t);
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("批量创建过程中发生错误: " + t.getMessage())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
            
            @Override
            public void onCompleted() {
                // 客户端发送完所有请求后会调用这个方法
                log.info("批量创建用户完成: 成功{}个, 失败{}个", successCount.get(), failureCount.get());
                
                CreateUserResponse response = CreateUserResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage(String.format("批量创建完成：成功%d个，失败%d个", 
                                successCount.get(), failureCount.get()))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
    
    /**
     * 用户聊天 - 双向流式RPC
     * 
     * 这种RPC类型：客户端和服务器都可以发送多个消息（双向流）
     * 适用于实时通信的场景
     * 
     * @param responseObserver 响应观察者
     * @return 请求观察者
     */
    @Override
    public StreamObserver<ChatMessage> userChat(StreamObserver<ChatMessage> responseObserver) {
        log.info("开始用户聊天会话");
        
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                log.info("收到聊天消息: userId={}, message={}", message.getUserId(), message.getMessage());
                
                // 模拟服务器处理聊天消息并回复
                ChatMessage response = ChatMessage.newBuilder()
                        .setUserId(0)  // 0表示系统/服务器
                        .setMessage("服务器收到你的消息: " + message.getMessage())
                        .setTimestamp(System.currentTimeMillis())
                        .build();
                
                responseObserver.onNext(response);
            }
            
            @Override
            public void onError(Throwable t) {
                log.error("聊天过程中发生错误", t);
            }
            
            @Override
            public void onCompleted() {
                log.info("聊天会话结束");
                
                // 发送告别消息
                ChatMessage farewell = ChatMessage.newBuilder()
                        .setUserId(0)
                        .setMessage("聊天会话已结束，再见！")
                        .setTimestamp(System.currentTimeMillis())
                        .build();
                
                responseObserver.onNext(farewell);
                responseObserver.onCompleted();
            }
        };
    }
    
    /**
     * 将JPA实体转换为Proto消息
     * 
     * @param userEntity JPA用户实体
     * @return Proto用户消息
     */
    private User convertToProtoUser(UserEntity userEntity) {
        return User.newBuilder()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setEmail(userEntity.getEmail())
                .setAge(userEntity.getAge())
                .setCreatedAt(userEntity.getCreatedAt())
                .build();
    }
} 