package me.veso.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDetailsDto {
    private String username;
    private String passwordHash;
    private String role;
    private String status;
}
