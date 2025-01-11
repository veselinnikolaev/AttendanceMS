package me.veso.categoryservice.dto;

import lombok.Getter;

@Getter
public class CategoryUpdateDto {
    private String name;
    private Long checkerId;
    private Long[] attendantsIds;
}
