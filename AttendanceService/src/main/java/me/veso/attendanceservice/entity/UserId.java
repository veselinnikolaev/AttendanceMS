package me.veso.attendanceservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class UserId extends ObjectWithId {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public UserId setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
