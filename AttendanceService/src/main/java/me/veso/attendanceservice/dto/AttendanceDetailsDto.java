package me.veso.attendanceservice.dto;

import me.veso.attendanceservice.entity.Attendance;

import java.time.LocalDateTime;

public record AttendanceDetailsDto(
        Long id,
        String status,
        Long userId,
        String categoryId,
        LocalDateTime createdAt
) {
    public AttendanceDetailsDto(Attendance attendance) {
        this(
                attendance.getId(),
                attendance.getStatus(),
                attendance.getUser().getId(),
                attendance.getCategory().getCategoryId(),
                attendance.getCreatedAt()
        );
    }
}

