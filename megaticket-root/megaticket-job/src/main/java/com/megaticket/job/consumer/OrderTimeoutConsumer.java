package com.megaticket.job.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 订单超时消息消费者
 * 处理15分钟未支付的订单自动关单
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = "order-timeout-topic",
    consumerGroup = "order-timeout-consumer-group"
)
public class OrderTimeoutConsumer implements RocketMQListener<String> {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到订单超时消息: {}", message);
            
            Map<String, Object> msgBody = objectMapper.readValue(message, Map.class);
            Long orderId = Long.parseLong(msgBody.get("orderId").toString());
            Long scheduleId = Long.parseLong(msgBody.get("scheduleId").toString());
            List<Map<String, Integer>> seats = (List<Map<String, Integer>>) msgBody.get("seats");
            
            log.info("处理订单超时, orderId={}, scheduleId={}", orderId, scheduleId);
            
        } catch (Exception e) {
            log.error("处理订单超时消息失败", e);
        }
    }
}
