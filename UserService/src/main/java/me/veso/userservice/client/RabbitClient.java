package me.veso.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.config.MessageQueueConfig;
import me.veso.userservice.dto.UserStatusDto;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyStatusUpdated(UserStatusDto userStatusDto) {
        try {
            log.debug("Sending status update for user: {}", userStatusDto);
            rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "user.status.updated", userStatusDto);
            log.info("User status update sent successfully.");
        } catch (AmqpException e) {
            log.error("Failed to send user status update: {}", e.getMessage(), e);
        }
    }
}
