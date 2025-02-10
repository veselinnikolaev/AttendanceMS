package me.veso.categoryservice.mapper;

import me.veso.categoryservice.dto.CategoryDetailsDto;
import me.veso.categoryservice.entity.Category;
import me.veso.categoryservice.entity.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "checkerId", source = "checker.userId")
    @Mapping(target = "attendantsIds", source = "attendants",
            qualifiedByName = "mapAttendantIds")
    CategoryDetailsDto toCategoryDetailsDto(Category category);

    @Named("mapAttendantIds")
    default List<Long> mapAttendantIds(List<UserId> attendants){
        return (attendants != null) ?
                attendants.stream().map(UserId::getUserId).toList() :
                Collections.emptyList();
    }
}