package me.veso.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.CategoryClient;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryClient client;
    private final CategoryService self;

    @Transactional
    public void delete(String categoryId) {
        log.info("Deleting category with id {}", categoryId);
        try {
            self.findByCategoryId(categoryId);
            repository.deleteByCategoryId(categoryId);
            evictCategoryCache(categoryId);
        } catch (Exception e) {
            log.error("Category with id {} not found", categoryId);
        }
    }

    @Cacheable(value = "categoriesByCategoryId", key = "#categoryId")
    public CategoryId saveIfNotExists(String categoryId) {
        log.debug("Checking if category {} exists", categoryId);

        return repository.findByCategoryId(categoryId).orElseGet(() -> {
            try {
                return CompletableFuture.supplyAsync(() -> client.getCategoryForId(categoryId))
                        .thenApply(existingCategory -> {
                            if (existingCategory == null) {
                                log.warn("Category with id {} does not exist", categoryId);
                                throw new RuntimeException("Category with id " + categoryId + " does not exist");
                            }
                            log.info("Saving new category with id {}", categoryId);
                            return repository.save(new CategoryId().setCategoryId(categoryId));
                        })
                        .exceptionally(ex -> {
                            log.error("Failed to fetch category ID {}: {}", categoryId, ex.getMessage());
                            throw new RuntimeException("Failed to fetch category", ex);
                        }).get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to process category: " + categoryId, e);
            }
        });
    }

    @Cacheable(value = "categoriesByCategoryId", key = "#categoryId")
    public CategoryId findByCategoryId(String categoryId) {
        log.debug("Fetching category with id {}", categoryId);
        return repository.findByCategoryId(categoryId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found", categoryId);
                    return new RuntimeException("Category with id " + categoryId + " not found");
                });
    }

    @CacheEvict(value = "categoriesByCategoryId", key = "#categoryId")
    public void evictCategoryCache(String categoryId) {
        log.info("Evicting cache for category with id {}", categoryId);
    }
}
