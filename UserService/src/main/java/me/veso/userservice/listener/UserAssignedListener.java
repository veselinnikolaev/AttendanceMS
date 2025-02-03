package me.veso.userservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.service.CategoryService;
import me.veso.userservice.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RabbitListener(queues = "users.assigning.queue")
@RequiredArgsConstructor
public class UserAssignedListener {
    private final UserService userService;
    private final CategoryService categoryService;

    @RabbitHandler
    public void handleAssigned(Map<String, Object> payload){
        List<Long> userIds = Arrays.asList((Long[]) payload.get("userIds"));
        String categoryId = (String) payload.get("categoryId");

        CategoryId category = categoryService.saveIfNotExists(categoryId);

        List<User> users = userService.findAllByIdIn(userIds);

        users.forEach(u -> u.addCategory(category));

        userService.saveAll(users);
    }
}
