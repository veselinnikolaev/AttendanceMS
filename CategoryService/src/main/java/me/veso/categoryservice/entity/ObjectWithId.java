package me.veso.categoryservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class ObjectWithId {
    @Id
    private String id;

    private boolean deleted;
}
