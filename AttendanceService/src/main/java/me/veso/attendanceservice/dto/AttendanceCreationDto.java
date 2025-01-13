package me.veso.attendanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class AttendanceCreationDto {
    @NotBlank(message = "Status is required")
    private String status;
    @Positive(message = "User ID must be positive number")
    @NotNull(message = "User ID is required")
    private Long userId;
    @Positive(message = "Category ID must be positive number")
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
