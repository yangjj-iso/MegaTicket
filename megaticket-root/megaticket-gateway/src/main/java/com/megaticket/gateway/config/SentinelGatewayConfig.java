package com.megaticket.gateway.config;


import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.megaticket.common.result.Result;
import com.megaticket.common.result.ResultCode;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Sentinel 网关限流配置
 * author Yang JunJie
 * date 2026/1/12
 */
@Configuration
public class SentinelGatewayConfig {

    /**
     * 初始化限流规则
     * param null
     * return null
     */
    @PostConstruct
    public void init(){
        // 自定义限流异常处理器
        BlockRequestHandler blockRequestHandler=(serverWebExchange, throwable) -> {
            Result<Object> result=Result.error(ResultCode.Too_Many_REQUESTS);
            // 返回自定义的限流响应
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BodyInserters.fromValue(result));
        };

        // 设置自定义的限流异常处理器
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
