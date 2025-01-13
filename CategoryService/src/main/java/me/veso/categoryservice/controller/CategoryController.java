package me.veso.categoryservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDetailsDto> createCategory(@RequestBody CategoryCreationDto categoryCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryCreationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDetailsDto> updateCategory(
            @Positive(message = "Category id must be positive") @PathVariable("id") Long id,
            @Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryUpdateDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<CategoryDetailsDto> assignAttendantsToCategory(
            @Positive(message = "Category id must be positive") @PathVariable("id") Long id,
            @Valid @RequestBody List<Long> attendantsIds) {
        return ResponseEntity.ok(categoryService.assignAttendantsToCategory(id, attendantsIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailsDto> getCategory(
            @Positive(message = "Category id must be positive") @PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }
}
