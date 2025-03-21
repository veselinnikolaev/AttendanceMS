package me.veso.attendanceservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.dto.CategoryDeletionErrorDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyAttendanceCreated(List<AttendanceDetailsDto> attendanceDetailsDto){
        rabbitTemplate.convertAndSend("notification.direct", "notify.attendance.created", attendanceDetailsDto);
    }

    public void notifyDeletionError(CategoryDeletionErrorDto categoryDeletionErrorDto){
        rabbitTemplate.convertAndSend("category.direct", "category.deleted.error", categoryDeletionErrorDto);
    }
}
