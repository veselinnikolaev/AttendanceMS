package me.veso.attendanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AttendanceCreationDto(
        @NotEmpty(message = "Users are required")
        List<UserStatusDto> usersStatuses,

        @NotBlank(message = "Category ID is required")
        String categoryId
) {}

