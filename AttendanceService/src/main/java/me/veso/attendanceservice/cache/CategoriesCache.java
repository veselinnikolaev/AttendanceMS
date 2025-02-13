package me.veso.attendanceservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriesCache {
    private final CategoryIdRepository categoryIdRepository;
    private final CacheManager cacheManager;

    @PostConstruct
    public void preloadCache() {
        List<CategoryId> all = categoryIdRepository.findAll();
        if (all.isEmpty()) {
            return;
        }

        Cache categoriesByCategoryIdCache = cacheManager.getCache("categoriesByCategoryId");

        if (categoriesByCategoryIdCache != null) {
            categoriesByCategoryIdCache.clear();
            all.forEach(category -> categoriesByCategoryIdCache.put(category.getCategoryId(), category));
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache() {
        preloadCache();
    }
}
