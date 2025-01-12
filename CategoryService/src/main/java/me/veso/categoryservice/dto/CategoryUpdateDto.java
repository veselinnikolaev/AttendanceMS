package me.veso.categoryservice.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoryUpdateDto {
    private String name;
    private Long checkerId;
    private List<Long> attendantsIds;
}
