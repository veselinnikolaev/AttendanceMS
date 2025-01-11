package me.veso.categoryservice.dto;

import lombok.Getter;

@Getter
public class CategoryCreationDto {
    private String name;
    private Long checkerId;
    private Long[] attendantsIds;
}
