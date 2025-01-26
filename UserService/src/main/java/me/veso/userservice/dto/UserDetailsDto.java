package me.veso.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsDto {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private List<Long> categories;

    public UserDetailsDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.processedAt = user.getProcessedAt();
        this.categories = user.getCategories().stream().map(CategoryId::getCategoryId).collect(Collectors.toList());
    }
}
