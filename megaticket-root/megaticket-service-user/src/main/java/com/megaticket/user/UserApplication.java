package com.megaticket.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 * @author Yang JunJie
 * @since 2026/1/19
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.megaticket.user.mapper")
@ComponentScan(basePackages = {"com.megaticket.user", "com.megaticket.common"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
