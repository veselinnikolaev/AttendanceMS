package me.veso.attendanceservice.entity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class UserId extends ObjectWithId {
    private Long userId;

    public UserId setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
