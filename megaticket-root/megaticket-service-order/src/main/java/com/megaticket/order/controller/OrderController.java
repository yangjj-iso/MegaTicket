package com.megaticket.order.controller;

import com.megaticket.common.result.Result;
import com.megaticket.order.dto.CreateOrderRequest;
import com.megaticket.order.entity.Order;
import com.megaticket.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * @author Yang JunJie
 * @since 2026/1/19
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/create")
    public Result<Order> createOrder(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreateOrderRequest request
    ) {
        Order order = orderService.createOrder(userId, request);
        return Result.success(order);
    }
    
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long orderId
    ) {
        Order order = orderService.getOrderDetail(userId, orderId);
        return Result.success(order);
    }
    
    @GetMapping("/list")
    public Result<List<Order>> getUserOrders(@RequestHeader("X-User-Id") Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }
    
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long orderId
    ) {
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }
}
