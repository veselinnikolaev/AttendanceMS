package me.veso.categoryservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.categoryservice.dto.CategoryDeletionErrorDto;
import me.veso.categoryservice.service.CategoryService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "category.deleted.error.queue")
@RequiredArgsConstructor
@Slf4j
public class CategoryDeletedErrorListener {
    private final CategoryService categoryService;

    @RabbitHandler
    public void handleDeletedCategory(CategoryDeletionErrorDto categoryDeletionErrorDto) {
        categoryService.undeleteCategoryById(categoryDeletionErrorDto.categoryId());
    }
}
