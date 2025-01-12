package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryIdService  {
    private final CategoryIdRepository categoryIdRepository;

    public CategoryId saveIdLongIfNotExists(Long id) {
        List<Long> categoryIds = categoryIdRepository.findAll().stream().map(CategoryId::getCategoryId).toList();
        if(categoryIds.contains(id)) {
            return categoryIdRepository.findByCategoryId(id).orElseThrow(() -> new RuntimeException("Category not found"));
        }
        CategoryId categoryId = new CategoryId().setCategoryId(id);
        return categoryIdRepository.save(categoryId);
    }
}
