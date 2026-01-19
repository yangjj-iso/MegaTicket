package com.megaticket.cinema;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 影院服务启动类
 * author Yang JunJie
 * since 2026/1/14
 */
@SpringBootApplication
@EnableCaching
@MapperScan("com.megaticket.cinema.mapper")
public class CinemaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);
    }
}
