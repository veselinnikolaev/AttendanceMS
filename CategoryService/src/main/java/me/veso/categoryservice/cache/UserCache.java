package me.veso.categoryservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCache {
    private final UserIdRepository userIdRepository;
    private final CacheManager cacheManager;

    @PostConstruct
    public void preloadCache() {
        List<UserId> users = userIdRepository.findAll();
        if (users.isEmpty()) {
            return;
        }

        Cache usersByIdCache = cacheManager.getCache("usersById");
        Cache usersByIdsCache = cacheManager.getCache("usersByIds");

        if (usersByIdCache != null) {
            usersByIdCache.clear();
            users.forEach(user -> usersByIdCache.put(user.getUserId(), user));
        }

        if (usersByIdsCache != null) {
            usersByIdsCache.clear();
            usersByIdsCache.put(users.stream()
                    .map(UserId::getUserId)
                    .toList().hashCode(), users);
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache(){
        preloadCache();
    }
}
