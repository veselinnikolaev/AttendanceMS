package me.veso.attendanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record UserStatusDto(
        @NotBlank(message = "Status is required")
        @Pattern(regexp = "^(present|absent)$", message = "Status must be either present or absent")
        String status,

        @Positive(message = "User ID must be a positive number")
        @NotNull(message = "User ID is required")
        Long userId
) {}
