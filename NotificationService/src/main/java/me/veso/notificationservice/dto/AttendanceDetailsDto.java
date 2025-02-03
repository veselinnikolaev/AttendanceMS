package me.veso.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDetailsDto {
    private Long id;
    private String status;
    private Long userId;
    private String categoryId;
    private LocalDateTime createdAt;
}
