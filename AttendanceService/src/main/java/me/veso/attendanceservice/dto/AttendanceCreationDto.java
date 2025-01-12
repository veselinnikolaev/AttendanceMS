package me.veso.attendanceservice.dto;

import lombok.Getter;

@Getter
public class AttendanceCreationDto {
    private String status;
    private Long userId;
    private Long categoryId;
}
