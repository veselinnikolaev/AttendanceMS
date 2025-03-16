package me.veso.userservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.RabbitClient;
import me.veso.userservice.dto.CategoryDeletionErrorDto;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.service.CategoryService;
import me.veso.userservice.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RabbitListener(queues = "category.deleted.queue")
@RequiredArgsConstructor
@Slf4j
public class CategoryDeletedListener {
    private final RabbitClient rabbitClient;
    private final CategoryService categoryService;
    private final UserService userService;

    @RabbitHandler
    public void handleDeletedCategory(String id) {
        log.debug("Received category deletion event for ID: {}", id);
try {
    CategoryId category = categoryService.findByCategoryId(id);

    List<User> users = userService.findAllByCategoryId(id);
    if (users.isEmpty()) {
        log.info("No users found for category {}. Proceeding with deletion.", id);
    } else {
        log.debug("Removing category {} from {} users.", id, users.size());
        users.forEach(user -> user.removeCategory(category));
        userService.saveAll(users);
        log.info("Successfully updated {} users after category {} deletion.", users.size(), id);
    }

    categoryService.delete(id);
    log.info("Successfully deleted category with ID: {}", id);
}catch (Exception ex){
    rabbitClient.notifyCategoryDeletedError(new CategoryDeletionErrorDto(id, ex.getMessage()));
}
    }
}
