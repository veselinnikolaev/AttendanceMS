package me.veso.categoryservice.dto;

import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.entity.UserId;

import java.util.List;

public record CategoryDetailsDto(
        String id,
        String name,
        Long checkerId,
        List<Long> attendantsIds
) {
    public CategoryDetailsDto(Category category) {
        this(
                category.getId(),
                category.getName(),
                category.getChecker().getUserId(),
                category.getAttendants().stream().map(UserId::getUserId).toList()
        );
    }
}
