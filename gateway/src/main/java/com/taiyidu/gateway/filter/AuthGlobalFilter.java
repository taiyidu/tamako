package com.taiyidu.gateway.filter;

import com.taiyidu.gateway.exception.UnauthorizedException;
import com.taiyidu.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    //白名单
    private static List<String> whitelist = null;
    //从配置文件中获取白名单

    static {
        //加载白名单
        try (
                InputStream resourceAsStream = AuthGlobalFilter.class.getResourceAsStream("/security-whitelist.properties");
        ) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist= new ArrayList<>(strings);
        } catch (Exception e) {
            log.error("加载/security-whitelist.properties出错:{}",e.getMessage());
            e.printStackTrace();
        }


    }

    private final JwtUtil jwtUtil;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    /**
     * 核心过滤器逻辑
     * @param exchange 包含当前请求和响应的上下文
     * @param chain 过滤器链，调用 chain.filter() 才会继续往下走
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
//        log.info("请求路径: {}", path);
        log.info("whitelist:{}", whitelist);
        // 1. 白名单放行判断
        for (String white : whitelist) {
            // 使用 AntPathMatcher 支持 /** 等通配符
            if (PATH_MATCHER.match(white, path)) {
                log.info("白名单放行: {}", path);
                return chain.filter(exchange);
            }
        }

        // 2. 从请求头中获取 Token
        // 使用 getFirst 避免请求头不存在时抛出空指针异常
        String header = request.getHeaders().getFirst("Authorization");

        // 3. 检查 Token 格式是否正确 (标准格式为 "Bearer 空格 Token")
        if (header == null || !header.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token 缺失或格式错误");
        }

        // 截掉前缀 "Bearer " (共 7 个字符)，取出真正的 JWT 字符串，并去除首尾空格
        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            return unauthorized(exchange, "Token 为空");
        }

        // 4. 核心：使用公钥验证 Token 的合法性（验签名 + 验过期时间）
        try {
            // 你的 JwtUtil 底层会自动验签名和校验过期时间
            Claims claims = jwtUtil.parseToken(token);

            // 获取用户 ID (通常签发时放在了 subject 里)
            String userId = claims.getSubject();

            // 5. 鉴权通过，透传用户信息给下游微服务
            ServerHttpRequest mutated = request.mutate()
                    .header("X-User-Id", userId == null ? "" : userId)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            // 抛出异常，带上具体原因给前端
            throw new UnauthorizedException("Token 已过期，请重新登录");
        } catch (Exception e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            throw new UnauthorizedException("Token 无效或已被篡改");
        }
    }

    /**
     * 封装未授权响应的方法
     * @param exchange 上下文
     * @param reason 错误原因 (目前仅用于代码逻辑清晰，未写入响应体)
     * @return 结束响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        // 设置 HTTP 响应状态码为 401 Unauthorized
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        // 结束响应，不调用 chain.filter()，请求会被直接拦截，不会到达下游微服务
        return exchange.getResponse().setComplete();
    }

    /**
     * 过滤器执行顺序
     * 返回 -1，数字越小优先级越高，确保在路由转发前尽早执行鉴权
     */
    @Override
    public int getOrder() {
        return -1;
    }
}