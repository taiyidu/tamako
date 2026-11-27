package com.taiyidu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 本文案金就起到一个log的作用，方便查看请求的请求的转发，但是不能看到咋转发到底是不是成功的
 */

@Slf4j
@Component
public class GatewayLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 打印请求方法、路径
        log.info("【网关请求】Method: {}, Path: {}", request.getMethod(), request.getURI().getPath());

//        // 关键：打印 Authorization 头，看看 Token 还在不在
//        String authHeader = request.getHeaders().getFirst("Authorization");
//        if (authHeader != null) {
//            // 可以只打印前缀，避免完整 Token 刷屏，或者直接打印完整内容排查
//            log.info("【网关请求】Authorization Header: {}", authHeader);
//        } else {
//            log.warn("【网关请求】Authorization Header 缺失！");
//        }

        // 继续执行过滤器链，放行请求
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 返回一个较小的值，确保这个过滤器尽早执行，在网关转发之前就能看到日志
        return -100;
    }
}
