package me.veso.userservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.repository.CategoryRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCache {
    private final CategoryRepository categoryRepository;
    private final CacheManager cacheManager;

    @PostConstruct
    public void preloadCache() {
        List<CategoryId> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        Cache categoriesByCategoryIdCache = cacheManager.getCache("categoriesByCategoryId");

        if (categoriesByCategoryIdCache != null) {
            categoriesByCategoryIdCache.clear();
            categories.forEach(category -> categoriesByCategoryIdCache.put(category.getCategoryId(), category));
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache() {
        preloadCache();
    }
}
