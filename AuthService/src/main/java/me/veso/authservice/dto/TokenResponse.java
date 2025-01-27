package me.veso.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private boolean isValid;
    private String username;
    private String role;
}
