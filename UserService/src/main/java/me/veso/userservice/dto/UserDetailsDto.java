package me.veso.userservice.dto;

import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
) {
    public UserDetailsDto(User user) {
        this(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getProcessedAt(),
                user.getCategories().stream().map(CategoryId::getCategoryId).collect(Collectors.toList()));
    }
}
