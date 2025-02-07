package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.CategoryClient;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryIdService {
    private final CategoryIdRepository categoryIdRepository;
    private final CategoryClient client;

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

                        return CompletableFuture.supplyAsync(() -> categoryIdRepository.findByCategoryId(id)
                                .orElseGet(() -> {
                                    log.info("Category ID {} does not exist in the repository. Saving it.", id);
                                    return categoryIdRepository.save(new CategoryId().setCategoryId(id));
                                }));
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

    public void delete(CategoryId category) {
        log.info("Deleting category with ID {} from repository.", category.getCategoryId());
        categoryIdRepository.delete(category);
        log.info("Category with ID {} deleted successfully.", category.getCategoryId());
    }
}
