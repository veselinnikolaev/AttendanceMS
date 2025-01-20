package me.veso.categoryservice.entity;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ObjectWithId {
    @Id
    private String id;
}
