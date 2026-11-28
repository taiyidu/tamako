package com.taiyidu.taiyiducommon.config;

import com.taiyidu.taiyiducommon.holder.GatewayHeaderAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// 1. 开启方法级安全，让 @PreAuthorize 注解生效
@EnableGlobalMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 2. 关闭 CSRF，因为微服务是无状态的
            .csrf().disable()
            // 3. 关闭 Session，使用无状态认证
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 4. 【关键】关闭默认的表单登录和 Basic 认证，防止前端弹窗
            .httpBasic().disable()
            .formLogin().disable()
            // 5. 所有请求都放行，交给我们的自定义 Filter 和 @PreAuthorize 去判断
            .authorizeRequests()
            .anyRequest().permitAll() 
            .and()
            // 6. 将你写的 GatewayHeaderAuthFilter 加入过滤器链
            .addFilterBefore(new GatewayHeaderAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            // 7. 自定义异常处理（前端不会弹窗，而是收到 JSON）
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401, \"msg\":\"未认证或登录已过期\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403, \"msg\":\"权限不足\"}");
            });

        return http.build();
    }
}