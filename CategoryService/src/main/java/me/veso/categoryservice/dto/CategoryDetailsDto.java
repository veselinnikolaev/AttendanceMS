package me.veso.categoryservice.dto;

import lombok.Getter;
import lombok.Setter;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.entity.UserId;

import java.util.List;

@Getter
@Setter
public class CategoryDetailsDto {
    private String id;
    private String name;
    private Long checkerId;
    private List<Long> attendantsIds;

    public CategoryDetailsDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.checkerId = category.getChecker().getUserId();
        this.attendantsIds = category.getAttendants().stream().map(UserId::getUserId).toList();
    }
}
