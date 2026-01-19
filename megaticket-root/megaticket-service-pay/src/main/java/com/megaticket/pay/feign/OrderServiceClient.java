package com.megaticket.pay.feign;

import com.megaticket.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 订单服务Feign客户端
 * @author Yang JunJie
 * @since 2026/1/19
 */
@FeignClient(name = "megaticket-service-order", path = "/api/v1/order")
public interface OrderServiceClient {
    
    @PostMapping("/{orderId}/paid")
    Result<Void> markOrderPaid(@PathVariable("orderId") Long orderId);
}
