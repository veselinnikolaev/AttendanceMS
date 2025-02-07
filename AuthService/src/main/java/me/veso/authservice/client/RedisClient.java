package me.veso.authservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisClient {
    private final StringRedisTemplate stringRedisTemplate;

    public void saveToken(String token, long expirationInSeconds){
        stringRedisTemplate.opsForValue().set(token, "blacklisted", expirationInSeconds, TimeUnit.SECONDS);
    }

    public boolean isPresent(String token){
        Boolean isPresent = stringRedisTemplate.hasKey(token);
        if(isPresent == null){
            return false;
        }
        return isPresent;
    }
}
