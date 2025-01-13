package me.veso.categoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryCreationDto {
    @NotBlank(message = "Name is required")
    private String name;
    @Positive(message = "Checker ID must be positive number")
    @NotNull(message = "Checker ID is required")
    private Long checkerId;
    @NotBlank(message = "Attendants IDs are required")
    private List<@Positive(message = "Attendant ID must be positive number") Long> attendantsIds;
}
