package me.veso.categoryservice.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Document(collection = "categories")
@Getter
@NoArgsConstructor
public class Category extends ObjectWithId {
    @Field("name")
    private String name;

    @DBRef
    private UserId checker;

    @DBRef
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
