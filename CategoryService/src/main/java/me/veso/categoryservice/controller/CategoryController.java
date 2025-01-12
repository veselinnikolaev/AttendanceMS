package me.veso.categoryservice.controller;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetailsDto createCategory(@RequestBody CategoryCreationDto categoryCreationDto) {
        return categoryService.createCategory(categoryCreationDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDetailsDto updateCategory(@PathVariable("id") Long id, @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return categoryService.updateCategory(id, categoryUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
    }

    @PostMapping("/{id}/assign")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDetailsDto assignAttendantsToCategory(@PathVariable("id") Long id, @RequestBody List<Long> attendantsIds) {
        return categoryService.assignAttendantsToCategory(id, attendantsIds);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDetailsDto getCategory(@PathVariable("id") Long id) {
        return categoryService.getCategory(id);
    }
}
