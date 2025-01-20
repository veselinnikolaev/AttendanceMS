package me.veso.attendanceservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "categories")
public class CategoryId extends ObjectWithId {
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    public CategoryId setCategoryId(Long id) {
        this.categoryId = id;
        return this;
    }
}
