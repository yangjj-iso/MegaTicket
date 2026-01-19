package com.megaticket.pay.service;

import cn.hutool.core.util.IdUtil;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.ResultCode;
import com.megaticket.pay.dto.PaymentCallbackRequest;
import com.megaticket.pay.dto.PaymentRequest;
import com.megaticket.pay.entity.Payment;
import com.megaticket.pay.feign.OrderServiceClient;
import com.megaticket.pay.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务类
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentMapper paymentMapper;
    private final OrderServiceClient orderServiceClient;
    
    public Payment createPayment(Long userId, PaymentRequest request) {
        if (userId == null || request.getOrderId() == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        Payment payment = new Payment();
        payment.setId(IdUtil.getSnowflakeNextId());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPayMethod(request.getPayMethod());
        payment.setTransactionId(IdUtil.simpleUUID());
        payment.setStatus(0);
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        
        paymentMapper.insert(payment);
        
        log.info("创建支付记录成功, paymentId={}, orderId={}", payment.getId(), request.getOrderId());
        
        return payment;
    }
    
    public void handlePaymentCallback(PaymentCallbackRequest request) {
        if (request.getTransactionId() == null || request.getOrderId() == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        log.info("收到支付回调, transactionId={}, orderId={}, status={}", 
            request.getTransactionId(), request.getOrderId(), request.getStatus());
        
        if ("SUCCESS".equals(request.getStatus())) {
            try {
                orderServiceClient.markOrderPaid(request.getOrderId());
                log.info("订单支付成功, orderId={}", request.getOrderId());
            } catch (Exception e) {
                log.error("更新订单状态失败, orderId={}", request.getOrderId(), e);
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
        }
    }
}
