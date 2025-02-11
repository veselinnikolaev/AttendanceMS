package me.veso.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.User;
import me.veso.userservice.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheManager cacheManager;
    private final UserService userService;

    @PostConstruct
    public void preloadCache() {
        List<User> users = userService.getAll();

        cacheManager.getCache("users").put("all", users);
        for (User user : users) {
            cacheManager.getCache("usersById").put(user.getId(), user);
            cacheManager.getCache("usersByStatus").put(user.getStatus(), user);
            Cache usersByCategoryId = cacheManager.getCache("usersByCategoryId");
            user.getCategories().forEach(category -> {
                usersByCategoryId.put(category.getCategoryId(), user);
            });
        }
    }
}
