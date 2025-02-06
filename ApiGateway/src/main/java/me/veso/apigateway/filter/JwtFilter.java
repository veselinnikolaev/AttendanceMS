package me.veso.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.apigateway.client.AuthClient;
import me.veso.apigateway.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements GlobalFilter, Ordered {
    private final AuthClient authClient;

    @Value("${free-resources.urls:}")
    private List<String> freeResourceUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip filtering for free resources
        if (freeResourceUrls.stream().anyMatch(request.getURI().getPath()::startsWith)) {
            return chain.filter(exchange);
        }

        // Extract token
        String token = extractToken(request);
        if (token == null) {
            return handleAuthError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
        }

        // Validate token & check blacklist asynchronously
        CompletableFuture<ResponseEntity<Boolean>> blacklistFuture = CompletableFuture.supplyAsync(() -> authClient.checkBlacklist(token));
        CompletableFuture<ResponseEntity<TokenResponse>> tokenFuture = CompletableFuture.supplyAsync(() -> authClient.validateToken(token));

        // Wait for all futures to complete (blocking call)
        CompletableFuture.allOf(blacklistFuture, tokenFuture).join();

        Boolean isBlacklisted = extractBodyOrDefault(blacklistFuture, "Failed to fetch token blacklist status: " + token, false);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            return handleAuthError(exchange, "Token is blacklisted", HttpStatus.UNAUTHORIZED);
        }

        TokenResponse tokenResponse = extractBodyOrDefault(tokenFuture, "Failed to fetch token status: " + token, null);
        if (tokenResponse == null || !tokenResponse.isValid()) {
            return handleAuthError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);  // Continue if valid
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }
        String authHeader = authHeaders.get(0);
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    private Mono<Void> handleAuthError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(("{\"error\": \"" + message + "\"}").getBytes())));
    }

    private <T> T extractBodyOrDefault(CompletableFuture<ResponseEntity<T>> future, String errorMessage, T defaultValue) {
        try {
            return future.get().getBody();
        } catch (Exception e) {
            log.error("{} - Reason: {}", errorMessage, e.getMessage());
            return defaultValue;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
