package me.veso.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.dto.CategoryDeletionErrorDto;
import me.veso.userservice.dto.UserStatusDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyStatusUpdated(UserStatusDto userStatusDto) {
        rabbitTemplate.convertAndSend("notification.direct", "notify.status.updated", userStatusDto);
    }

    public void notifyCategoryDeletedError(CategoryDeletionErrorDto categoryDeletionErrorDto) {
        rabbitTemplate.convertAndSend("category.direct", "category.deleted.error", categoryDeletionErrorDto);
    }
}
