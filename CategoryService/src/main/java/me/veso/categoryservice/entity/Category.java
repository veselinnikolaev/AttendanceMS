package me.veso.categoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.veso.categoryservice.dto.CategoryUpdateDto;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Category extends ObjectWithId {
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private UserId checker;
    @ManyToMany
    private List<UserId> attendants;

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public Category setChecker(UserId checker) {
        this.checker = checker;
        return this;
    }

    public Category setAttendants(List<UserId> attendants) {
        this.attendants = attendants;
        return this;
    }

    public Category addAttendants(List<UserId> attendants) {
        this.attendants.addAll(attendants);
        return this;
    }
}
