package com.taiyidu.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taiyidu.gateway.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(-1) // 必须小于 -1，优先级高于 Spring 默认的异常处理器
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 如果响应已经提交过了，直接结束
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> result = new HashMap<>();
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String msg = "系统异常，请稍后再试";

        // 1. 捕获我们自定义的鉴权异常
        if (ex instanceof UnauthorizedException) {
            statusCode = ((UnauthorizedException) ex).getCode();
            msg = ex.getMessage();
        } 
        // 2. 捕获路由找不到 (404) 异常
        else if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            statusCode = HttpStatus.NOT_FOUND.value();
            msg = "请求的微服务不存在或已下线";
        }
        // 3. 其他未预料的异常
        else {
            log.error("网关全局异常: ", ex);
        }

        response.setStatusCode(HttpStatus.valueOf(statusCode));
        result.put("code", statusCode);
        result.put("msg", msg);
        result.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("网关响应序列化失败: ", e);
            return response.setComplete();
        }
    }
}