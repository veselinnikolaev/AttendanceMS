package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserIdService userIdService;

    public CategoryDetailsDto createCategory(CategoryCreationDto categoryCreationDto) {
        Category category = new Category()
                .setName(categoryCreationDto.getName())
                .setChecker(userIdService.saveIdLong(categoryCreationDto.getCheckerId()))
                .setAttendants(userIdService.saveIdsLong(categoryCreationDto.getAttendantsIds()));

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public CategoryDetailsDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        Category category = new Category()
                .setName(categoryUpdateDto.getName())
                .setChecker(userIdService.saveIdLong(categoryUpdateDto.getCheckerId()))
                .setAttendants(userIdService.saveIdsLong(categoryUpdateDto.getAttendantsIds()));
        category.setId(id);

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryDetailsDto assignAttendantsToCategory(Long id, Long[] attendantsIds) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.addAttendants(userIdService.saveIdsLong(attendantsIds));
        categoryRepository.save(category);
        return new CategoryDetailsDto(category);
    }

    public CategoryDetailsDto getCategory(Long id) {
        return new CategoryDetailsDto(categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found")));
    }
}
