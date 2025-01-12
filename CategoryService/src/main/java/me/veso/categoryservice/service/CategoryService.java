package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserIdService userIdService;

    public CategoryDetailsDto createCategory(CategoryCreationDto categoryCreationDto) {
        Category category = new Category()
                .setName(categoryCreationDto.getName())
                .setChecker(userIdService.saveIdLongIfNotExists(categoryCreationDto.getCheckerId()))
                .setAttendants(userIdService.saveIdsLongIfNotExist(categoryCreationDto.getAttendantsIds()));

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public CategoryDetailsDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        Category category = new Category()
                .setName(categoryUpdateDto.getName())
                .setChecker(userIdService.saveIdLongIfNotExists(categoryUpdateDto.getCheckerId()))
                .setAttendants(userIdService.saveIdsLongIfNotExist(categoryUpdateDto.getAttendantsIds()));
        category.setId(id);

        return new CategoryDetailsDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryDetailsDto assignAttendantsToCategory(Long id, List<Long> attendantsIds) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.addAttendants(userIdService.saveIdsLongIfNotExist(attendantsIds));
        categoryRepository.save(category);
        return new CategoryDetailsDto(category);
    }

    public CategoryDetailsDto getCategory(Long id) {
        return new CategoryDetailsDto(categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found")));
    }
}
