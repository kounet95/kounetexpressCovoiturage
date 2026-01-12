package com.covoiturage.aiagentservice.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(10)
public class McpAuthFilter extends OncePerRequestFilter {

    @Value("${mcp.server.enabled:true}")
    private boolean enabled;

    @Value("${mcp.auth.api-key:}")
    private String apiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !enabled || !path.startsWith("/mcp/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }
        String auth = request.getHeader("Authorization");
        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "MCP API key not configured");
            return;
        }
        if (auth == null || !auth.startsWith("Bearer ") || !apiKey.equals(auth.substring(7))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid MCP API key");
            return;
        }
        filterChain.doFilter(request, response);
    }
}