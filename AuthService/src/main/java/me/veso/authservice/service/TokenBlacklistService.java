package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.client.RedisClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisClient redisClient;

    public void blacklistToken(String token, long expirationInSeconds) {
        redisClient.saveToken(token, expirationInSeconds);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisClient.isPresent(token));
    }
}
