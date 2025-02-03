package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.notificationservice.dto.UserDetailsDto;
import me.veso.notificationservice.dto.UserStatusDto;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RabbitListener(queues = "status.updated.queue")
@RequiredArgsConstructor
public class StatusUpdatedListener {
    private final MailService mailService;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    @RabbitHandler
    public void handleStatusUpdated(UserStatusDto userStatusDto){
        UserDetailsDto user = client.getForEntity(userServiceUrl + "/{id}", UserDetailsDto.class, userStatusDto.id()).getBody();

        mailService.send(user.email(), "Status Updated",
                """
                Hi %s,
                Your account status has been updated!
                Current status: %s.
                """.formatted(user.username(), userStatusDto.status()));
    }
}
