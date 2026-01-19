package com.megaticket.seat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 座位服务启动类
 * 项目核心模块：使用 Redis Bitfield + Lua 脚本实现高并发座位管理
 *
 * @author Yang JunJie
 * @since 2026/1/14
 */
@SpringBootApplication(scanBasePackages = {"com.megaticket.seat", "com.megaticket.common"})
@MapperScan("com.megaticket.seat.mapper")
@EnableCaching
public class SeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatApplication.class, args);
    }
}
