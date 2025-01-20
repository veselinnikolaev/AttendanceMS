package me.veso.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
public class User extends ObjectWithId {
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String passwordHash;
    @Column(nullable = false)
    private String role;
    private String status = "pending";
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime processedAt;
    @ManyToMany
    @JoinTable(name = "user_category",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
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
