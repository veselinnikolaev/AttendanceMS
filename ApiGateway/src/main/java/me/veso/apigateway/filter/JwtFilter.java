package me.veso.apigateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.apigateway.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${jwt.public-urls}")
    private List<String> freeResourceUrls;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return freeResourceUrls.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException {
        try {
            String token = extractToken(request);
            validateTokenAndCheckBlacklist(token);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            handleAuthenticationError(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            handleAuthenticationError(response, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }

    private void validateTokenAndCheckBlacklist(String token) {
        try {
            // First check blacklist
            ResponseEntity<Boolean> blacklistResponse = checkBlacklist(token);
            if (blacklistResponse.getBody() != null && blacklistResponse.getBody()) {
                throw new RuntimeException("Token is blacklisted");
            }

            // Then validate token
            ResponseEntity<TokenResponse> validationResponse = validateToken(token);
            if (validationResponse.getBody() == null || !validationResponse.getBody().isValid()) {
                throw new RuntimeException("Invalid JWT token");
            }
        } catch (HttpClientErrorException e) {
            log.error("Error communicating with auth service", e);
            throw new RuntimeException("Error validating token");
        }
    }

    private ResponseEntity<TokenResponse> validateToken(String token) {
        String url = UriComponentsBuilder
                .fromUriString(authServiceUrl)
                .path("/validate")
                .queryParam("token", token)
                .build()
                .toUriString();

        return restTemplate.getForEntity(url, TokenResponse.class);
    }

    private ResponseEntity<Boolean> checkBlacklist(String token) {
        String url = UriComponentsBuilder
                .fromUriString(authServiceUrl)
                .path("/blacklist/check")
                .queryParam("token", token)
                .build()
                .toUriString();

        return restTemplate.getForEntity(url, Boolean.class);
    }

    private void handleAuthenticationError(
            HttpServletResponse response,
            String message,
            HttpStatus status
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.getWriter().flush();
    }
}
