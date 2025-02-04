package me.veso.categoryservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.config.MessageQueueConfig;
import me.veso.categoryservice.dto.CategoryDeletedEvent;
import me.veso.categoryservice.dto.UsersAssignedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyUsersAssigned(UsersAssignedEvent usersAssignedEvent){
        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned", usersAssignedEvent);
    }

    public void notifyCategoryDeleted(CategoryDeletedEvent categoryDeletedEvent){
        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "category.deleted", categoryDeletedEvent.categoryId());
    }
}
