package me.veso.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class CategoryId extends ObjectWithId {
    @Column(nullable = false)
    private Long categoryId;
}
