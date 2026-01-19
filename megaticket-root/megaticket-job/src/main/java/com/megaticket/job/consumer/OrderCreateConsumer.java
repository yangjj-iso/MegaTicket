package com.megaticket.job.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单创建消息消费者
 * 用于数据同步和后续处理
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = "order-create-topic",
    consumerGroup = "order-create-consumer-group"
)
public class OrderCreateConsumer implements RocketMQListener<String> {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到订单创建消息: {}", message);
            
            Map<String, Object> msgBody = objectMapper.readValue(message, Map.class);
            Long orderId = Long.parseLong(msgBody.get("orderId").toString());
            
            log.info("订单创建成功, 进行后续处理, orderId={}", orderId);
            
        } catch (Exception e) {
            log.error("处理订单创建消息失败", e);
        }
    }
}
