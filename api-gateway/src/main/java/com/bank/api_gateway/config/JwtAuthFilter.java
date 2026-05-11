package com.bank.api_gateway.config;

import com.bank.api_gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        if (
                path.equals("/api/v1/auth/login") ||
                        path.equals("/api/v1/auth/register") ||
                        path.equals("/api/v1/auth/verify-register") ||
                        path.startsWith("/api/v1/auth/forgot-password") ||
                        path.equals("/api/v1/auth/resend-otp") ||
                        path.equals("/api/v1/auth/refresh-token") ||
                        path.equals("/error")
        ) {
            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }

        String token = authHeader.substring(7);

        try {
            jwtService.extractAllClaims(token);

        } catch (Exception e) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }

        ServerHttpRequest request =
                exchange.getRequest()
                        .mutate()
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                authHeader
                        )
                        .build();

        return chain.filter(
                exchange.mutate()
                        .request(request)
                        .build()
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}