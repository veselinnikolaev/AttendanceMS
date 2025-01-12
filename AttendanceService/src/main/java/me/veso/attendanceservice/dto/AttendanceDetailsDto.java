package me.veso.attendanceservice.dto;

import lombok.Getter;
import lombok.Setter;
import me.veso.attendanceservice.entity.Attendance;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDetailsDto {
    private Long id;
    private String status;
    private Long userId;
    private Long categoryId;
    private LocalDateTime createdAt;

    public AttendanceDetailsDto(Attendance attendance) {
        this.id = attendance.getId();
        this.status = attendance.getStatus();
        this.userId = attendance.getUser().getId();
        this.categoryId = attendance.getCategory().getCategoryId();
        this.createdAt = attendance.getCreatedAt();
    }
}
