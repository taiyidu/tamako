package com.taiyidu.taiyiducommon.holder;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        // 如果网关注入了 ID，说明已经认证过
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 如果需要角色权限，可以从 payload 中解析，这里简单演示给一个默认角色
            // 实际建议：网关注入 X-User-Roles，微服务直接读取
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userId, 
                    userRole, // 将载荷作为 credentials 或 details 存入，方便业务代码获取
                    Collections.singletonList(new SimpleGrantedAuthority(userRole))
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}