package me.veso.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import me.veso.userservice.validation.annotation.PasswordMatch;

@Getter
@PasswordMatch
public class UserRegisterDto {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    @NotBlank(message = "Role is required")
    private String role;
}
