package me.veso.categoryservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryCreationDto;
import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.dto.CategoryUpdateDto;
import me.veso.categoryservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDetailsDto> createCategory(@Valid @RequestBody CategoryCreationDto categoryCreationDto) {
        //TODO: Maybe do not include users in the dto, because there is endpoint for assigning users
        //TODO: Notify assigned users for this category
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryCreationDto));
    }

    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<CategoryDetailsDto> updateCategory(
            @NotBlank(message = "Category id is required") @PathVariable("id") String id,
            @Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryUpdateDto));
    }

    @DeleteMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@NotBlank(message = "Category id is required") @PathVariable("id") String id) {
        categoryService.deleteCategory(id);
    }

    @PostMapping("/{id}/assign")
    @Validated
    public ResponseEntity<CategoryDetailsDto> assignAttendantsToCategory(
            @NotBlank(message = "Category id is required") @PathVariable("id") String id,
            @Valid @RequestBody List<Long> attendantsIds) {
        //TODO: Notify assigned users for this category
        return ResponseEntity.ok(categoryService.assignAttendantsToCategory(id, attendantsIds));
    }

    @GetMapping("/{id}")
    @Validated
    public ResponseEntity<CategoryDetailsDto> getCategory(
            @NotBlank(message = "Category id is required") @PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }
}
