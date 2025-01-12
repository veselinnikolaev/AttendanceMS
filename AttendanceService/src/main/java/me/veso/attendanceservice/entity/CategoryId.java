package me.veso.attendanceservice.entity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class CategoryId extends ObjectWithId {
    private Long categoryId;

    public CategoryId setCategoryId(Long id) {
        this.categoryId = id;
        return this;
    }
}
