package me.veso.categoryservice.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record CategoryUpdateDto(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotNull(message = "Checker ID is required")
        @Positive(message = "Checker ID must be a positive number")
        Long checkerId,

        @NotEmpty(message = "At least one attendant is required")
        List<@NotNull(message = "Attendant ID cannot be null") @Positive(message = "Attendant ID must be a positive number") Long> attendantsIds
) {}
