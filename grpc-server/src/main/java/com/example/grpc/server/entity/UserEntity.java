package com.example.grpc.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户实体类
 * 
 * 使用JPA注解映射到数据库表
 * 使用Lombok注解自动生成getter/setter等方法
 * 
 * 这个实体类对应数据库中的用户表
 */
@Entity
@Table(name = "users")  // 指定数据库表名
@Data  // Lombok注解：自动生成getter/setter/toString/equals/hashCode方法
@NoArgsConstructor  // Lombok注解：生成无参构造函数
@AllArgsConstructor  // Lombok注解：生成全参构造函数
public class UserEntity {
    
    /**
     * 用户ID - 主键，自动递增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名 - 唯一，不能为空
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 邮箱地址 - 唯一，不能为空
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * 年龄
     */
    @Column(nullable = false)
    private Integer age;
    
    /**
     * 创建时间 - 自动设置
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    /**
     * 在持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
} 