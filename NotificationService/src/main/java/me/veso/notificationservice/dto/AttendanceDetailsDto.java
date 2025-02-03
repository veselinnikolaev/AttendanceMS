package me.veso.notificationservice.dto;

import java.time.LocalDateTime;

public record AttendanceDetailsDto(
        Long id,
        String status,
        Long userId,
        String categoryId,
        LocalDateTime createdAt
) {}
