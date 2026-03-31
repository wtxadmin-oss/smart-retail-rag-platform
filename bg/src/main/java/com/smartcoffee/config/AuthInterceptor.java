package com.smartcoffee.config;

import com.smartcoffee.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        
        // 需要登录的路径前缀
        boolean isAdminPath = uri.startsWith("/api/admin/") || uri.equals("/api/admin");
        boolean isUserPath = uri.startsWith("/api/user/") || uri.equals("/api/user")
                          || uri.startsWith("/api/cart/") || uri.equals("/api/cart")
                          || uri.startsWith("/api/orders/") || uri.equals("/api/orders")
                          || uri.equals("/api/auth/change-password");
        
        // 放行公开接口
        if (!isAdminPath && !isUserPath) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token无效\"}");
            return false;
        }

        String token = authHeader.substring(7);
        Claims claims = JwtUtils.parseToken(token);
        
        if (claims == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token无效\"}");
            return false;
        }

        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        
        // 存入 request 方便后续使用
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentUserRole", role);

        // 检查管理员权限
        if (isAdminPath && !"ADMIN".equals(role)) {
            response.setStatus(403);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无权限访问管理员接口\"}");
            return false;
        }

        return true;
    }
}
