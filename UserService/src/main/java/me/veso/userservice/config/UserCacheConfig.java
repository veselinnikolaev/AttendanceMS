package me.veso.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.repository.UserRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserCacheConfig {

    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    @PostConstruct
    public void preloadCache() {
        //"users", "usersByIds", "usersByCategory", "usersById", "usersByUsername"
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            return;
        }

        Cache usersCache = cacheManager.getCache("users");
        Cache usersByIdsCache = cacheManager.getCache("usersByIds");
        Cache usersByCategoryCache = cacheManager.getCache("usersByCategory");
        Cache usersByIdCache = cacheManager.getCache("usersById");
        Cache usersByUsernameCache = cacheManager.getCache("usersByUsername");

        if (usersCache != null) {
            usersCache.put("all", users);
        }

        if (usersByIdsCache != null) {
            usersByIdsCache.put(users.stream()
                    .map(User::getId)
                    .toList().hashCode(), users);
        }

        if (usersByIdCache != null) {
            users.forEach(user -> usersByIdCache.put(user.getId(), user));
        }

        if (usersByUsernameCache != null) {
            users.forEach(user -> usersByUsernameCache.put(user.getUsername(), user));
        }

        if (usersByCategoryCache != null) {
            Map<String, List<User>> usersByCategory = new HashMap<>();

            for (User user : users) {
                if (user.getCategories() != null) {
                    for (CategoryId category : user.getCategories()) {
                        usersByCategory
                                .computeIfAbsent(category.getCategoryId(), k -> new ArrayList<>())
                                .add(user);
                    }
                }
            }

            usersByCategory.forEach(usersByCategoryCache::put);
        }

        System.out.println("Caches preloaded successfully!");
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache() {
        preloadCache();
    }
}
