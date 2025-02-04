package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.notificationservice.client.UserClient;
import me.veso.notificationservice.dto.UserStatusDto;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RabbitListener(queues = "status.updated.queue")
@RequiredArgsConstructor
@Slf4j
public class StatusUpdatedListener {
    private final MailService mailService;
    private final UserClient client;

    @RabbitHandler
    public void handleStatusUpdated(UserStatusDto userStatusDto) {
        CompletableFuture
                .supplyAsync(() -> client.getUserForId(userStatusDto.id()))
                .exceptionally(ex -> {
                    log.error("Failed to fetch user details for ID {}: {}", userStatusDto.id(), ex.getMessage());
                    return null;
                })
                .thenAccept(user -> {
                    if (user == null) {
                        log.warn("Skipping email notification for user ID {}, user not found.", userStatusDto.id());
                        return;
                    }

                    mailService.send(user.email(), "Status Updated",
                            String.format("""
                                    Hi %s,
                                    Your account status has been updated!
                                    Current status: %s.
                                    """, user.username(), userStatusDto.status()));
                });
    }
}
