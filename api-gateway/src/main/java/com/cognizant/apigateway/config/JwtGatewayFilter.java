package com.cognizant.apigateway.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final GatewayJwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ✅ PUBLIC ENDPOINTS
        if (path.startsWith("/auth/")
                || path.startsWith("/actuator/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // ✅ Missing token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "JWT token is missing");
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.parseToken(token);

            // ✅ Expired token
            if (jwtUtil.isExpired(claims)) {
                return unauthorized(exchange, "JWT token has expired");
            }

            String userId = claims.get("userId").toString();
            List<String> roles = claims.get("roles", List.class);

            // ✅ REAL HTTP HEADERS
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", String.join(",", roles))
                    .build();

            return chain.filter(
                    exchange.mutate().request(mutatedRequest).build()
            );

        } catch (Exception ex) {
            return unauthorized(exchange, "Invalid JWT token");
        }
    }

    /**
     * ✅ Centralized 401 handler with JSON body
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
              "status": 401,
              "error": "UNAUTHORIZED",
              "message": "%s",
              "path": "%s",
              "timestamp": "%s"
            }
            """.formatted(
                message,
                exchange.getRequest().getPath(),
                Instant.now().toString()
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // run early
    }
}