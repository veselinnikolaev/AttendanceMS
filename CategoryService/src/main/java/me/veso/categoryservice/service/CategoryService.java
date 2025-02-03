package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.config.MessageQueueConfig;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.repository.CategoryRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserIdService userIdService;
    private final RabbitTemplate rabbitTemplate;

    public CategoryDetailsDto createCategory(CategoryCreationDto categoryCreationDto) {
        Category category = new Category()
                .setName(categoryCreationDto.name())
                .setChecker(userIdService.saveIdLongIfNotExists(categoryCreationDto.checkerId()))
                .setAttendants(userIdService.saveIdsLongIfNotExist(categoryCreationDto.attendantsIds()));

        Category categorySaved = categoryRepository.save(category);

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned",
                Map.of("checkerId", categoryCreationDto.checkerId(),
                        "attendantsIds", categoryCreationDto.attendantsIds(),
                        "categoryId", categorySaved.getId()));

        return new CategoryDetailsDto(categorySaved);
    }

    public CategoryDetailsDto updateCategory(String id, CategoryUpdateDto categoryUpdateDto) {
        Category category = new Category()
                .setName(categoryUpdateDto.name())
                .setChecker(userIdService.saveIdLongIfNotExists(categoryUpdateDto.checkerId()))
                .setAttendants(userIdService.saveIdsLongIfNotExist(categoryUpdateDto.attendantsIds()));
        category.setId(id);

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned",
                Map.of("checkerId", categoryUpdateDto.checkerId(),
                        "attendantsIds", categoryUpdateDto.attendantsIds(),
                        "categoryId", id));

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public void deleteCategory(String id) {
        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "category.deleted", id);

        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryDetailsDto assignAttendantsToCategory(String id, List<Long> attendantsIds) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.addAttendants(userIdService.saveIdsLongIfNotExist(attendantsIds));

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned",
                Map.of("checkerId", category.getChecker().getUserId(),
                        "attendantsIds", attendantsIds,
                        "categoryId", id));

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public CategoryDetailsDto getCategory(String id) {
        return new CategoryDetailsDto(categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found")));
    }
}
