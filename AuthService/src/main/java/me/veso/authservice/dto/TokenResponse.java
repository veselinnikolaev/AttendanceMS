package me.veso.authservice.dto;

public record TokenResponse(boolean isValid,
                            String username,
                            String role) {
}
