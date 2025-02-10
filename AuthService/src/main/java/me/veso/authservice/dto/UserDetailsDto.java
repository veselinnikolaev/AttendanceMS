package me.veso.authservice.dto;

public record UserDetailsDto(String username,
                             String passwordHash,
                             String role,
                             String status) {
}
