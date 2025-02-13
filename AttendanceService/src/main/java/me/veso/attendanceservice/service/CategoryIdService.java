package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.CategoryClient;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryIdService {
    private final CategoryIdRepository categoryIdRepository;
    private final CategoryClient client;
    private final CategoryIdService self;

    public CategoryId saveIdLongIfNotExists(String id) {
        log.info("Checking if category with ID {} exists.", id);

        try {
            return CompletableFuture.supplyAsync(() -> client.getCategoryForId(id))
                    .thenCompose(category -> {
                        if (category == null) {
                            log.warn("Category with ID {} does not exist.", id);
                            throw new RuntimeException("Category with ID " + id + " does not exist");
                        }

                        log.info("Category with ID {} found, processing the ID.", id);

                        return CompletableFuture.supplyAsync(() -> self.getOrSaveIfNotExistsByCategoryId(id));
                    }).exceptionally(ex -> {
                        log.error("Failed to fetch category for ID {}: {}", id, ex.getMessage());
                        return null;
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching category for ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error fetching category for ID " + id, e);
        }
    }

    public CategoryId findByCategoryId(String id) {
        log.info("Fetching category with ID {} from repository.", id);
        return categoryIdRepository.findByCategoryId(id)
                .orElseThrow(() -> {
                    log.error("Category with ID {} not found in repository.", id);
                    return new RuntimeException("Category with ID " + id + " not found");
                });
    }

    @Transactional
    public void delete(String categoryId) {
        log.info("Deleting category with ID {} from repository.", categoryId);
        categoryIdRepository.deleteByCategoryId(categoryId);
        log.info("Category with ID {} deleted successfully.", categoryId);
        self.evictFromCache(categoryId);
    }

    @Transactional
    public CategoryId saveCategory(String categoryId){
        CategoryId saved = categoryIdRepository.save(new CategoryId().setCategoryId(categoryId));
        return self.putCategoryInCache(saved);
    }

    @Cacheable(value = "categoriesByCategoryId", key = "#categoryId")
    public CategoryId getOrSaveIfNotExistsByCategoryId(String categoryId){
        return categoryIdRepository.findByCategoryId(categoryId)
                .orElseGet(() -> {
                    log.info("Category ID {} does not exist in the repository. Saving it.", categoryId);
                    return self.saveCategory(categoryId);
                });
    }

    @CachePut(value = "categoriesByCategoryId", key = "#categoryId.categoryId")
    public CategoryId putCategoryInCache(CategoryId categoryId){
        log.info("Category with category id {} put in cache", categoryId.getCategoryId());
        return categoryId;
    }

    @CacheEvict(value = "categoriesByCategoryId", key = "#categoryId")
    public void evictFromCache(String categoryId){
        log.info("Category with category id {} evicted from cache", categoryId);
    }
}
