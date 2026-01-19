package com.megaticket.order.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megaticket.order.entity.Order;
import com.megaticket.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * RocketMQ事务消息监听器
 * 确保订单创建与消息发送的原子性
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class OrderTransactionListener implements RocketMQLocalTransactionListener {
    
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            Order order = (Order) arg;
            
            log.info("执行本地事务: 创建订单, orderId={}", order.getId());
            
            orderMapper.insert(order);
            
            log.info("本地事务执行成功, orderId={}", order.getId());
            return RocketMQLocalTransactionState.COMMIT;
            
        } catch (Exception e) {
            log.error("本地事务执行失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
    
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try {
            String orderId = msg.getHeaders().get("orderId", String.class);
            
            if (orderId == null) {
                log.warn("事务回查: orderId为空");
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            
            Order order = orderMapper.selectById(Long.parseLong(orderId));
            
            if (order != null) {
                log.info("事务回查: 订单存在, orderId={}", orderId);
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                log.warn("事务回查: 订单不存在, orderId={}", orderId);
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            
        } catch (Exception e) {
            log.error("事务回查异常", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
