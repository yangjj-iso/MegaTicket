package com.megaticket.pay.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付回调请求DTO(模拟第三方支付回调)
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Data
public class PaymentCallbackRequest {
    
    private String transactionId;
    
    private Long orderId;
    
    private BigDecimal amount;
    
    private String status;
}
