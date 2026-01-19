package com.megaticket.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megaticket.common.result.Result;
import com.megaticket.common.result.ResultCode;
import com.megaticket.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Gateway全部认证类
 * author Yang JunJie
 * date 2026/1/12
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AuthGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //白名单
    private static final List<String> WHITELIST= List.of(
        "/api/user/login",           // 登录接口
        "/api/user/register",        // 注册接口
        "/api/cinema/list",          // 影院列表（公开）
        "/api/cinema/schedule",      // 排期查询（公开）
        "/api/seat/query",           // 座位查询（公开）
        "/actuator/**"               // 健康检查
    );

    /**
     * 认证过滤器
     * @param exchange 交换对象
     * @param chain 过滤器链
     * @return 返回过滤结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 临时禁用认证，放行所有请求（仅用于压力测试）
        // TODO: 测试完成后请删除以下代码，恢复正常认证
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", "1")  // 添加默认用户ID用于测试
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
        
        /* 原认证逻辑（已临时禁用）
        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        //2.判断是否在白名单内
        for(String url:WHITELIST){
            if(antPathMatcher.match(url,path)) return chain.filter(exchange);
        }

        //2.获取请求头中的token
        String token=request.getHeaders().getFirst(JwtUtil.HEADER_STRING);

        //3.验证token
        Long userId = null;
        try{
            if(token !=null && !token.isEmpty()) userId = jwtUtil.getUserId(token);
        }catch (Exception e){
            log.warn("Token验证失败:{}",e.getMessage());
        }

        //4.如果token无效，拒绝访问
        if(userId==null){
            return unauthorizeResponse(exchange);
        }

        //5.如果token有效，将用户ID传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
        */
    }

    /**
     * 辅助方法：响应未授权访问,生成401 Json响应
     * @param exchange 交换对象
     * @return 响应结果
     */
    private Mono<Void> unauthorizeResponse(ServerWebExchange exchange) {
        //1.设置响应状态码和头
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //2.创建响应体
        Result<Object> result = Result.error(ResultCode.UNAUTHORIZED);
        byte[] bytes;
        try {
            //3.将响应体转换为JSON字节数组
            bytes = new ObjectMapper().writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            bytes = "{\"code\":401,\"msg\":\"Unauthorized\"}".getBytes();
        }

        //4.写入响应体并返回
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器顺序，数字越小优先级越高
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
