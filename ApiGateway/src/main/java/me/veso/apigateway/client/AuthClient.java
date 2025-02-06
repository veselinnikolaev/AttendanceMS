package me.veso.apigateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.apigateway.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthClient {
    private final RestTemplate restTemplate;
    private final String authServiceUrl = "http://AUTH_SERVICE/auth";

    public ResponseEntity<TokenResponse> validateToken(String token) {
        return restTemplate.getForEntity(authServiceUrl + "?token={token}", TokenResponse.class, token);
    }

    public ResponseEntity<Boolean> checkBlacklist(String token) {
        return restTemplate.getForEntity(authServiceUrl + "?token={token}", Boolean.class, token);
    }
}
