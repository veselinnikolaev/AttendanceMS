package me.veso.userservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.repository.UserRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserCache {

    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    @PostConstruct
    public void preloadCache() {
        //"users", "usersByIds", "usersByCategory", "usersById", "usersByUsername"
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return;
        }

        Cache usersCache = cacheManager.getCache("users");
        Cache usersByIdsCache = cacheManager.getCache("usersByIds");
        Cache usersByCategoryCache = cacheManager.getCache("usersByCategory");
        Cache usersByIdCache = cacheManager.getCache("usersById");
        Cache usersByUsernameCache = cacheManager.getCache("usersByUsername");

        if (usersCache != null) {
            usersCache.clear();
            usersCache.put("all", users);
        }

        if (usersByIdsCache != null) {
            usersByIdsCache.clear();
            usersByIdsCache.put(users.stream()
                    .map(User::getId)
                    .toList().hashCode(), users);
        }

        if (usersByIdCache != null) {
            usersByIdCache.clear();
            users.forEach(user -> usersByIdCache.put(user.getId(), user));
        }

        if (usersByUsernameCache != null) {
            usersByUsernameCache.clear();
            users.forEach(user -> usersByUsernameCache.put(user.getUsername(), user));
        }

        if (usersByCategoryCache != null) {
            usersByCategoryCache.clear();
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
