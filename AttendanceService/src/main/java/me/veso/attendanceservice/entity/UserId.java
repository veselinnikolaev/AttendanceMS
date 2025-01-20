package me.veso.attendanceservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class UserId extends ObjectWithId {
    private Long userId;

    public UserId setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
