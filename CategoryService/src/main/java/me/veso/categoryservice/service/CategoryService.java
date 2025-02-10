package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.categoryservice.client.RabbitClient;
import me.veso.categoryservice.dto.*;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.mapper.CategoryMapper;
import me.veso.categoryservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserIdService userIdService;
    private final RabbitClient rabbitClient;
    private final CategoryMapper categoryMapper;

    public CategoryDetailsDto createCategory(CategoryCreationDto categoryCreationDto) {
        log.info("Creating category with name: {}", categoryCreationDto.name());

        Category category = new Category()
                .setName(categoryCreationDto.name())
                .setChecker(userIdService.saveIdLongIfNotExists(categoryCreationDto.checkerId()))
                .setAttendants(userIdService.saveIdsLongIfNotExist(categoryCreationDto.attendantsIds()));

        Category categorySaved = categoryRepository.save(category);
        log.info("Category created with ID: {}", categorySaved.getId());

        UsersAssignedEvent usersAssignedEvent = new UsersAssignedEvent(
                categoryCreationDto.checkerId(),
                categoryCreationDto.attendantsIds(),
                categorySaved.getId()
        );
        rabbitClient.notifyUsersAssigned(usersAssignedEvent);
        log.info("Users assigned event notified for category ID: {}", categorySaved.getId());

        return categoryMapper.toCategoryDetailsDto(categorySaved);
    }

    public CategoryDetailsDto updateCategory(String id, CategoryUpdateDto categoryUpdateDto) {
        log.info("Updating category with ID: {}", id);

        // Проверка дали категорията съществува
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with ID {} not found", id);
            return new RuntimeException("Category not found, ID " + id);
        });

        existingCategory.setName(categoryUpdateDto.name());
        existingCategory.setChecker(userIdService.saveIdLongIfNotExists(categoryUpdateDto.checkerId()));
        existingCategory.setAttendants(userIdService.saveIdsLongIfNotExist(categoryUpdateDto.attendantsIds()));

        log.info("Category with ID {} updated", id);

        UsersAssignedEvent usersAssignedEvent = new UsersAssignedEvent(
                categoryUpdateDto.checkerId(),
                categoryUpdateDto.attendantsIds(),
                id
        );
        rabbitClient.notifyUsersAssigned(usersAssignedEvent);
        log.info("Users assigned event notified for category ID: {}", id);

        return categoryMapper.toCategoryDetailsDto(categoryRepository.save(existingCategory));
    }

    public void deleteCategory(String id) {
        log.info("Deleting category with ID: {}", id);

        rabbitClient.notifyCategoryDeleted(new CategoryDeletedEvent(id));
        log.info("Category deleted event notified for category ID: {}", id);

        categoryRepository.deleteById(id);
        log.info("Category with ID {} deleted", id);
    }

    @Transactional
    public CategoryDetailsDto assignAttendantsToCategory(String id, List<Long> attendantsIds) {
        log.info("Assigning attendants to category with ID: {}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with ID {} not found", id);
            return new RuntimeException("Category not found, ID " + id);
        });
        category.addAttendants(userIdService.saveIdsLongIfNotExist(attendantsIds));

        log.info("Assigned attendants to category ID: {}", id);

        UsersAssignedEvent usersAssignedEvent = new UsersAssignedEvent(
                category.getChecker().getUserId(),
                attendantsIds,
                id
        );
        rabbitClient.notifyUsersAssigned(usersAssignedEvent);
        log.info("Users assigned event notified for category ID: {}", id);

        return categoryMapper.toCategoryDetailsDto(categoryRepository.save(category));
    }

    public CategoryDetailsDto getCategory(String id) {
        log.info("Fetching details for category with ID: {}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with ID {} not found", id);
            return new RuntimeException("Category not found, ID " + id);
        });

        log.info("Category with ID {} fetched", id);
        return categoryMapper.toCategoryDetailsDto(category);
    }
}
