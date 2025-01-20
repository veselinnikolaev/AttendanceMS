package me.veso.attendanceservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Attendance extends ObjectWithId {
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private boolean emailSent;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserId user;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryId category;

    public Attendance setStatus(String status) {
        this.status = status;
        return this;
    }

    public Attendance setUserId(UserId userId) {
        this.user = userId;
        return this;
    }

    public Attendance setCategoryId(CategoryId categoryId) {
        this.category = categoryId;
        return this;
    }
}
