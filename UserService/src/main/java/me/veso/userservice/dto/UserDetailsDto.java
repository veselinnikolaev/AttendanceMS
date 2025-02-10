package me.veso.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDetailsDto(
        Long id,
        String username,
        String email,
        String passwordHash,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime processedAt,
        List<String> categories
) {}
