package me.veso.attendanceservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.config.MessageQueueConfig;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyAssigmentCreated(AttendanceDetailsDto attendanceDetailsDto){
        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "attendance.created", attendanceDetailsDto);
    }
}
