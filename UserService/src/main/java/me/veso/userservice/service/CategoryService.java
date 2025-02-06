package me.veso.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.CategoryClient;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryClient client;

    public CategoryId saveIfNotExists(String categoryId) {
        log.debug("Checking if category {} exists", categoryId);
        CompletableFuture.supplyAsync(() -> client.getCategoryForId(categoryId))
                .thenAccept(category -> {
                    if (category == null) {
                        log.warn("Category with id {} does not exist", categoryId);
                        throw new RuntimeException("Category with id " + categoryId + " does not exist");
                    }
                }).exceptionally(ex -> {
                    log.error("Failed to fetch category details for ID {}: {}", categoryId, ex.getMessage());
                    return null;
                }).join();

        return repository.findByCategoryId(categoryId)
                .orElseGet(() -> {
                    log.info("Saving new category with id {}", categoryId);
                    return repository.save(new CategoryId().setCategoryId(categoryId));
                });
    }

    public CategoryId findByCategoryId(String id) {
        log.debug("Fetching category with id {}", id);
        return repository.findByCategoryId(id)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found", id);
                    return new RuntimeException("Category with id " + id + " not found");
                });
    }

    @Transactional
    public void delete(CategoryId category) {
        log.info("Deleting category with id {}", category.getCategoryId());
        repository.delete(category);
    }
}
