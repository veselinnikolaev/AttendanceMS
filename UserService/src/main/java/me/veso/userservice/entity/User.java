package me.veso.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class User extends ObjectWithId {
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;
    @Column(nullable = false)
    private String role;
    private String status = "pending";
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
    @ManyToMany
    private List<CategoryId> categories;

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public User setStatus(String status) {
        this.status = status;
        return this;
    }
}
