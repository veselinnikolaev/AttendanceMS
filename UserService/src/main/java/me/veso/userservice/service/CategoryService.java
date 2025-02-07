package me.veso.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.CategoryClient;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.repository.CategoryRepository;
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

    public CategoryId saveIfNotExists(String categoryId) {
        log.debug("Checking if category {} exists", categoryId);

        try {
            return CompletableFuture.supplyAsync(() -> client.getCategoryForId(categoryId))
                    .thenCompose(category -> {
                        if (category == null) {
                            log.warn("Category with id {} does not exist", categoryId);
                            throw new RuntimeException("Category with id " + categoryId + " does not exist");
                        }

                        return CompletableFuture.supplyAsync(() -> repository.findByCategoryId(categoryId)
                                .orElseGet(() -> {
                                    log.info("Saving new category with id {}", categoryId);
                                    return repository.save(new CategoryId().setCategoryId(categoryId));
                                }));
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to process category details for ID {}: {}", categoryId, ex.getMessage());
                        throw new RuntimeException("Failed to process category details", ex);
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
