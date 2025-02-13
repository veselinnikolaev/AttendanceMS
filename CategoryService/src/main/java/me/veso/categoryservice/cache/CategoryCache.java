package me.veso.categoryservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.repository.CategoryRepository;
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
    public void preloadCache(){
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            return;
        }

        Cache categoriesByIdCache = cacheManager.getCache("categoriesById");

        if (categoriesByIdCache != null){
            categoriesByIdCache.clear();
            categories.forEach(category -> categoriesByIdCache.put(category.getId(), category));
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache(){
        preloadCache();
    }
}
