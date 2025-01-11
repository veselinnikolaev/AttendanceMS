package me.veso.categoryservice.dto;

import lombok.Getter;
import lombok.Setter;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.entity.UserId;

@Getter
@Setter
public class CategoryDetailsDto {
    private String name;
    private Long checkerId;
    private Long[] attendantsIds;

    public CategoryDetailsDto(Category category) {
        this.name = category.getName();
        this.checkerId = category.getChecker().getUserId();
        this.attendantsIds = category.getAttendants().stream().map(UserId::getUserId).toArray(Long[]::new);
    }
}
