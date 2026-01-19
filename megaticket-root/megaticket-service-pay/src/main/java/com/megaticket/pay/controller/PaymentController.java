package com.megaticket.pay.controller;

import com.megaticket.common.result.Result;
import com.megaticket.pay.dto.PaymentCallbackRequest;
import com.megaticket.pay.dto.PaymentRequest;
import com.megaticket.pay.entity.Payment;
import com.megaticket.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * @author Yang JunJie
 * @since 2026/1/19
 */
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create")
    public Result<Payment> createPayment(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody PaymentRequest request
    ) {
        Payment payment = paymentService.createPayment(userId, request);
        return Result.success(payment);
    }
    
    @PostMapping("/callback")
    public Result<Void> paymentCallback(@RequestBody PaymentCallbackRequest request) {
        paymentService.handlePaymentCallback(request);
        return Result.success();
    }
}
