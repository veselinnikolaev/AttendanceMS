package me.veso.attendanceservice.mapper;

import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    @Mapping(target = "categoryId", expression = "category.categoryId")
    AttendanceDetailsDto toAttendanceDetailsDto(Attendance attendance);
}
