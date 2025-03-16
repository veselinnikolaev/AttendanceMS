package me.veso.categoryservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.dto.CategoryDeletedEvent;
import me.veso.categoryservice.dto.UsersAssignedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitClient {
    private final RabbitTemplate rabbitTemplate;

    public void notifyUsersAssigned(UsersAssignedEvent usersAssignedEvent){
        rabbitTemplate.convertAndSend("user.direct", "users.assigned", usersAssignedEvent);
    }

    public void notifyCategoryDeleted(CategoryDeletedEvent categoryDeletedEvent){
        rabbitTemplate.convertAndSend("user.direct", "category.deleted", categoryDeletedEvent.categoryId());
        rabbitTemplate.convertAndSend("attendance.direct", "category.deleted", categoryDeletedEvent.categoryId());
    }
}
