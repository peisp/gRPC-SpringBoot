package com.example.grpc.server.repository;

import com.example.grpc.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 * 
 * 继承JpaRepository提供基本的CRUD操作
 * Spring Data JPA会自动实现这个接口
 * 
 * 泛型参数：
 * - UserEntity: 实体类型
 * - Long: 主键类型
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * 根据用户名查找用户
     * 
     * Spring Data JPA会根据方法名自动生成查询
     * findBy + 属性名 + 查询条件
     * 
     * @param username 用户名
     * @return Optional包装的用户实体
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱地址
     * @return Optional包装的用户实体
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     * 
     * Spring Data JPA会生成: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱地址
     * @return 是否存在
     */
    boolean existsByEmail(String email);
} 