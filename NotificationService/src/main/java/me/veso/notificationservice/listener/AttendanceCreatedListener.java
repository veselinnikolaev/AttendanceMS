package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.notificationservice.dto.AttendanceDetailsDto;
import me.veso.notificationservice.dto.UserDetailsDto;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RabbitListener(queues = "attendance.created.queue")
@RequiredArgsConstructor
public class AttendanceCreatedListener {
    private final MailService mailService;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    @RabbitHandler
    public void handleAttendanceCreated(AttendanceDetailsDto attendanceDetailsDto){
        UserDetailsDto user = client.getForEntity(userServiceUrl + "/{id}", UserDetailsDto.class, attendanceDetailsDto.getUserId()).getBody();

        mailService.send(user.getEmail(), "New Attendance",
                """
                Hi %s,
                There is a new attendance for your profile!
                Attendance status: %s.
                """.formatted(user.getUsername(), attendanceDetailsDto.getStatus()));
    }
}
