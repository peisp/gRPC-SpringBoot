package com.example.grpc.client.controller;

import com.example.grpc.client.service.UserClientService;
import com.example.grpc.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 用户REST API控制器
 * 
 * 这个控制器将gRPC调用包装成REST API
 * 演示了如何在Web应用中使用gRPC客户端
 * 
 * 所有的HTTP请求会被转换为gRPC调用
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserClientService userClientService;
    
    /**
     * 创建用户 - POST /api/users
     * 
     * @param userRequest 用户创建请求
     * @return 创建结果
     */
    @PostMapping
    public Map<String, Object> createUser(@RequestBody CreateUserRequestDto userRequest) {
        log.info("收到HTTP创建用户请求: {}", userRequest);
        
        CreateUserResponse response = userClientService.createUser(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getAge()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.getSuccess());
        result.put("message", response.getMessage());
        
        if (response.hasUser()) {
            User user = response.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("age", user.getAge());
            userMap.put("createdAt", user.getCreatedAt());
            result.put("user", userMap);
        }
        
        return result;
    }
    
    /**
     * 获取用户 - GET /api/users/{id}
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable Long id) {
        log.info("收到HTTP获取用户请求: id={}", id);
        
        GetUserResponse response = userClientService.getUser(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.getSuccess());
        result.put("message", response.getMessage());
        
        if (response.hasUser()) {
            User user = response.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("age", user.getAge());
            userMap.put("createdAt", user.getCreatedAt());
            result.put("user", userMap);
        }
        
        return result;
    }
    
    /**
     * 获取所有用户 - GET /api/users（演示服务器流式RPC）
     * 
     * @return 所有用户列表
     */
    @GetMapping
    public Map<String, Object> getAllUsers() {
        log.info("收到HTTP获取所有用户请求（流式）");
        
        List<User> users = userClientService.getAllUsers();
        
        List<Map<String, Object>> userList = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("age", user.getAge());
            userMap.put("createdAt", user.getCreatedAt());
            userList.add(userMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取用户列表成功");
        result.put("users", userList);
        result.put("count", userList.size());
        
        return result;
    }
    
    /**
     * 更新用户 - PUT /api/users/{id}
     * 
     * @param id 用户ID
     * @param userRequest 更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, 
                                         @RequestBody UpdateUserRequestDto userRequest) {
        log.info("收到HTTP更新用户请求: id={}, request={}", id, userRequest);
        
        UpdateUserResponse response = userClientService.updateUser(
                id,
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getAge()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.getSuccess());
        result.put("message", response.getMessage());
        
        if (response.hasUser()) {
            User user = response.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("age", user.getAge());
            userMap.put("createdAt", user.getCreatedAt());
            result.put("user", userMap);
        }
        
        return result;
    }
    
    /**
     * 删除用户 - DELETE /api/users/{id}
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        log.info("收到HTTP删除用户请求: id={}", id);
        
        DeleteUserResponse response = userClientService.deleteUser(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.getSuccess());
        result.put("message", response.getMessage());
        
        return result;
    }
    
    /**
     * 批量创建用户 - POST /api/users/batch（演示客户端流式RPC）
     * 
     * @param userRequests 用户创建请求列表
     * @return 批量创建结果
     */
    @PostMapping("/batch")
    public Map<String, Object> batchCreateUsers(@RequestBody List<CreateUserRequestDto> userRequests) {
        log.info("收到HTTP批量创建用户请求: 数量={}", userRequests.size());
        
        // 转换为gRPC请求对象
        List<CreateUserRequest> grpcRequests = new ArrayList<>();
        for (CreateUserRequestDto dto : userRequests) {
            CreateUserRequest grpcRequest = CreateUserRequest.newBuilder()
                    .setUsername(dto.getUsername())
                    .setEmail(dto.getEmail())
                    .setAge(dto.getAge())
                    .build();
            grpcRequests.add(grpcRequest);
        }
        
        CreateUserResponse response = userClientService.batchCreateUsers(grpcRequests);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.getSuccess());
        result.put("message", response.getMessage());
        
        return result;
    }
    
    /**
     * 用户聊天 - POST /api/users/chat（演示双向流式RPC）
     * 
     * @param chatRequest 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public Map<String, Object> userChat(@RequestBody ChatRequestDto chatRequest) {
        log.info("收到HTTP聊天请求: 消息数量={}", chatRequest.getMessages().size());
        
        List<ChatMessage> responses = userClientService.userChat(chatRequest.getMessages());
        
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (ChatMessage chatMessage : responses) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("userId", chatMessage.getUserId());
            messageMap.put("message", chatMessage.getMessage());
            messageMap.put("timestamp", chatMessage.getTimestamp());
            responseList.add(messageMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "聊天完成");
        result.put("responses", responseList);
        
        return result;
    }
    
    // DTO类定义
    
    /**
     * 创建用户请求DTO
     */
    public static class CreateUserRequestDto {
        private String username;
        private String email;
        private int age;
        
        // 构造函数
        public CreateUserRequestDto() {}
        
        public CreateUserRequestDto(String username, String email, int age) {
            this.username = username;
            this.email = email;
            this.age = age;
        }
        
        // Getter和Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        @Override
        public String toString() {
            return "CreateUserRequestDto{username='" + username + "', email='" + email + "', age=" + age + "}";
        }
    }
    
    /**
     * 更新用户请求DTO
     */
    public static class UpdateUserRequestDto {
        private String username;
        private String email;
        private int age;
        
        // 构造函数
        public UpdateUserRequestDto() {}
        
        public UpdateUserRequestDto(String username, String email, int age) {
            this.username = username;
            this.email = email;
            this.age = age;
        }
        
        // Getter和Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        @Override
        public String toString() {
            return "UpdateUserRequestDto{username='" + username + "', email='" + email + "', age=" + age + "}";
        }
    }
    
    /**
     * 聊天请求DTO
     */
    public static class ChatRequestDto {
        private List<String> messages;
        
        // 构造函数
        public ChatRequestDto() {}
        
        public ChatRequestDto(List<String> messages) {
            this.messages = messages;
        }
        
        // Getter和Setter
        public List<String> getMessages() { return messages; }
        public void setMessages(List<String> messages) { this.messages = messages; }
        
        @Override
        public String toString() {
            return "ChatRequestDto{messages=" + messages + "}";
        }
    }
} 