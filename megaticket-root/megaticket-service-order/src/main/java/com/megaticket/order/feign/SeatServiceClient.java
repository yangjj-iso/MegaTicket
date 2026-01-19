package com.megaticket.order.feign;

import com.megaticket.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 座位服务Feign客户端
 * @author Yang JunJie
 * @since 2026/1/19
 */
@FeignClient(name = "megaticket-service-seat", path = "/api/v1/seat")
public interface SeatServiceClient {
    
    @PostMapping("/lock")
    Result<List<Map<String, Integer>>> lockSeats(
        @RequestParam("scheduleId") Long scheduleId,
        @RequestBody List<Map<String, Integer>> seats
    );
    
    @PostMapping("/release")
    Result<Integer> releaseSeats(
        @RequestParam("scheduleId") Long scheduleId,
        @RequestBody List<Map<String, Integer>> seats
    );
    
    @PostMapping("/sold")
    Result<Integer> markSeatsSold(
        @RequestParam("scheduleId") Long scheduleId,
        @RequestBody List<Map<String, Integer>> seats
    );
}
