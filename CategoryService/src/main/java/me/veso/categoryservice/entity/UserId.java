package me.veso.categoryservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "userIds")
@Getter
@Setter
public class UserId extends ObjectWithId {
    @Field(name = "user_id")
    private Long userId;

    public UserId setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
