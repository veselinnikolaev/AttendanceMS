package me.veso.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> categories;
}
