package com.megaticket.order.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.Result;
import com.megaticket.common.result.ResultCode;
import com.megaticket.order.dto.CreateOrderRequest;
import com.megaticket.order.entity.Order;
import com.megaticket.order.feign.SeatServiceClient;
import com.megaticket.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务类
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderMapper orderMapper;
    private final SeatServiceClient seatServiceClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;
    
    private static final int ORDER_EXPIRE_MINUTES = 15;
    private static final BigDecimal SEAT_PRICE = new BigDecimal("50.00");
    
    public Order createOrder(Long userId, CreateOrderRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        if (request.getScheduleId() == null || request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        Result<List<Map<String, Integer>>> lockResult = seatServiceClient.lockSeats(
            request.getScheduleId(), 
            request.getSeats()
        );
        
        if (lockResult.getCode() != 200) {
            throw new BusinessException(ResultCode.SEAT_ALREADY_LOCKED);
        }
        
        Order order = new Order();
        order.setId(IdUtil.getSnowflakeNextId());
        order.setUserId(userId);
        order.setScheduleId(request.getScheduleId());
        order.setCinemaId(1L);
        order.setCinemaName("示例影院");
        order.setHallId(1L);
        order.setHallName("1号厅");
        order.setMovieName("示例电影");
        order.setShowTime(LocalDateTime.now().plusDays(1));
        
        try {
            order.setSeatInfo(objectMapper.writeValueAsString(request.getSeats()));
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        
        order.setSeatCount(request.getSeats().size());
        order.setTotalPrice(SEAT_PRICE.multiply(new BigDecimal(request.getSeats().size())));
        order.setStatus(0);
        order.setTransactionId(IdUtil.simpleUUID());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setExpireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));
        
        try {
            Map<String, Object> msgBody = new HashMap<>();
            msgBody.put("orderId", order.getId());
            msgBody.put("scheduleId", order.getScheduleId());
            msgBody.put("seats", request.getSeats());
            
            org.springframework.messaging.Message<String> message = MessageBuilder
                .withPayload(objectMapper.writeValueAsString(msgBody))
                .setHeader("orderId", order.getId().toString())
                .build();
            
            rocketMQTemplate.sendMessageInTransaction(
                "order-create-topic",
                message,
                order
            );
            
            Map<String, Object> delayMsgBody = new HashMap<>();
            delayMsgBody.put("orderId", order.getId());
            delayMsgBody.put("scheduleId", order.getScheduleId());
            delayMsgBody.put("seats", request.getSeats());
            
            // 发送延迟消息，15分钟后触发（900000毫秒）
            rocketMQTemplate.syncSend(
                "order-timeout-topic",
                MessageBuilder.withPayload(objectMapper.writeValueAsString(delayMsgBody)).build(),
                3000,
                16  // 延迟级别16对应30分钟
            );
            
            log.info("订单创建成功, orderId={}, userId={}", order.getId(), userId);
            
        } catch (Exception e) {
            log.error("发送RocketMQ消息失败", e);
            seatServiceClient.releaseSeats(request.getScheduleId(), request.getSeats());
            throw new BusinessException(ResultCode.ORDER_CREATE_FAILED);
        }
        
        return order;
    }
    
    public Order getOrderDetail(Long userId, Long orderId) {
        if (userId == null || orderId == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        return order;
    }
    
    public List<Order> getUserOrders(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        QueryWrapper<Order> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.orderByDesc("create_time");
        
        return orderMapper.selectList(qw);
    }
    
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getOrderDetail(userId, orderId);
        
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_INVALID);
        }
        
        order.setStatus(3);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        try {
            List<Map<String, Integer>> seats = objectMapper.readValue(
                order.getSeatInfo(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );
            
            seatServiceClient.releaseSeats(order.getScheduleId(), seats);
            
            log.info("订单取消成功, orderId={}", orderId);
            
        } catch (Exception e) {
            log.error("释放座位失败, orderId={}", orderId, e);
        }
    }
}
