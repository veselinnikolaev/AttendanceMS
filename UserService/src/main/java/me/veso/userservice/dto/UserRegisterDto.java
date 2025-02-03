package me.veso.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import me.veso.userservice.validation.annotation.PasswordMatch;

@PasswordMatch
public record UserRegisterDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "^(admin|attendant|checker)$", message = "Role must be either attendant, checker or admin")
        String role
) {}
