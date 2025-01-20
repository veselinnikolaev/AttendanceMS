package me.veso.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "categories")
public class CategoryId extends ObjectWithId {
    @Column(nullable = false)
    private Long categoryId;
}
