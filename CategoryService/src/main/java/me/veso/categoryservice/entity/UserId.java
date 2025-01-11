package me.veso.categoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class UserId extends ObjectWithId {
    @Column(nullable = false)
    private Long userId;

    public UserId setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
