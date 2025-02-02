package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.config.MessageQueueConfig;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";
    private final RabbitTemplate rabbitTemplate;

    public UserId saveIdLongIfNotExists(Long id) {
        String status = client.getForObject(userServiceUrl + "/{id}/status", String.class, id);
        if(!"approved".equals(status)){
            throw new RuntimeException("User is not approved, actual status " + status);
        }

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "attendance.created", id);

        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }
}
