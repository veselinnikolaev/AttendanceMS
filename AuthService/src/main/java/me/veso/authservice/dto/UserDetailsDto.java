package me.veso.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDto {
    private String username;
    private String passwordHash;
    private String role;
    private String status;
}
