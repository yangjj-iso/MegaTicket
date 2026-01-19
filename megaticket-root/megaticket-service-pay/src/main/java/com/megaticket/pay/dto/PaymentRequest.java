package com.megaticket.pay.dto;

import lombok.Data;

/**
 * 支付请求DTO
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Data
public class PaymentRequest {
    
    private Long orderId;
    
    private String payMethod;
}
