package me.veso.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import me.veso.userservice.entity.User;

@Getter
@Setter
public class UserDetailsDto {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String role;

    public UserDetailsDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
    }
}
