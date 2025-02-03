package me.veso.userservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryDetailsDto {
    private String id;
    private String name;
    private Long checkerId;
    private List<Long> attendantsIds;
}
