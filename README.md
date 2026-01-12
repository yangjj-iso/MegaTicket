# MegaTicket 分布式票务系统

## 项目简介

MegaTicket 是一个基于微服务架构的高并发电影票务系统，旨在解决大规模用户同时在线选座、下单场景下的性能瓶颈与数据一致性问题。本项目深度实践了“高内聚、低耦合”的设计理念，采用 Spring Cloud Alibaba 技术栈，重点攻克了海量并发读写、分布式事务、服务高可用等技术难点。

## 技术架构

本项目采用 Maven 多模块架构，基于 JDK 21 和 Spring Boot 3 构建。

### 核心技术栈

*   **开发语言**: Java 21
*   **核心框架**: Spring Boot 3.1.5, Spring Cloud 2022.0.4, Spring Cloud Alibaba 2022.0.0.0
*   **服务治理**: Nacos (注册中心/配置中心), Sentinel (熔断限流)
*   **网关**: Spring Cloud Gateway
*   **数据库**: PostgreSQL 14, MyBatis Plus
*   **缓存**: Redis 7 (Redisson), Caffeine (本地缓存)
*   **消息队列**: RocketMQ 5.1.4 (事务消息, 延迟队列)
*   **工具库**: Hutool, Lombok

### 架构设计亮点

1.  **极致性能的选座系统**
    *   摒弃传统数据库行锁方案，采用 Redis Bitfield 存储座位状态，将座位图转化为二进制矩阵，极大降低内存占用。
    *   使用 Lua 脚本实现原子性选座与锁座操作，避免超卖问题，在单机 Redis 下可支撑万级 QPS。
    *   设计了从 Redis 到 MySQL 的异步数据同步机制，确保数据最终持久化。

2.  **多级缓存架构**
    *   针对“读多写少”的影院与排期数据，构建了 Caffeine (JVM 进程内缓存) + Redis (分布式缓存) 的多级缓存体系。
    *   有效抵御热点数据的高频查询，降低数据库负载，提升系统响应速度。

3.  **柔性分布式事务**
    *   订单支付流程采用 RocketMQ 事务消息方案，确保本地订单创建与消息发送的原子性。
    *   利用 RocketMQ 延迟消息实现“15分钟未支付自动关单”逻辑，替代传统的定时轮询任务，提升系统实时性与吞吐量。

4.  **高可用与容错**
    *   网关层集成 Sentinel，对热门电影抢票接口进行针对性限流，保护后端服务不被瞬时流量压垮。
    *   服务间调用采用 Feign，并配置合理的超时与重试策略。

## 模块说明

| 模块名称 | 描述 | 关键技术 |
| :--- | :--- | :--- |
| **megaticket-root** | 父工程 | 依赖版本管理 (DependencyManagement) |
| **megaticket-common** | 基础公共模块 | 通用 POJO, Result, GlobalException, Utils |
| **megaticket-gateway** | API 网关 | 统一鉴权, 动态路由, Sentinel 限流 |
| **megaticket-service-user** | 用户服务 | JWT 鉴权, 用户体系管理 |
| **megaticket-service-cinema** | 影院服务 | 影院/排期管理, Caffeine + Redis 多级缓存 |
| **megaticket-service-seat** | 座位服务 | **核心模块**, Redis Bitfield, Lua 脚本原子操作 |
| **megaticket-service-order** | 订单服务 | **核心模块**, RocketMQ 事务消息/延迟消息, 分库分表 |
| **megaticket-service-pay** | 支付服务 | 模拟第三方支付回调 |
| **megaticket-job** | 任务调度服务 | 消息消费, 数据兜底同步 |

## 环境要求

*   JDK 21+
*   Maven 3.8+
*   MySQL 8.0+
*   Redis 7.0+
*   RocketMQ 5.0+
*   Nacos 2.x

## 快速开始

1.  **环境准备**: 启动 Nacos, Redis, RocketMQ, MySQL 服务。
2.  **配置修改**: 修改各服务 `application.yml` 中的中间件连接地址。
3.  **基础服务启动**: 优先启动 Gateway 与 User 服务。
4.  **业务服务启动**: 启动 Cinema, Seat, Order 等核心服务。
5.  **验证**: 访问网关地址进行接口测试。

## 开发规范

*   遵循阿里巴巴 Java 开发手册。
*   代码提交需经过 Checkstyle 检查。
*   统一使用 `Result<T>` 进行接口返回。
