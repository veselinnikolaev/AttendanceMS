package me.veso.userservice.mapper;

import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "categories", source = "categories",
            qualifiedByName = "mapCategoryIds")
    UserDetailsDto toUserDetailsDto(User user);

    @Named("mapCategoryIds")
    default List<String> mapCategoryIds(List<CategoryId> categories) {
        return (categories != null) ?
                categories.stream().map(CategoryId::getCategoryId).toList() :
                Collections.emptyList();
    }
}