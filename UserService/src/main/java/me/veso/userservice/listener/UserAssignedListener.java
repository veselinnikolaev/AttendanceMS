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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RabbitListener(queues = "users.assigning.queue")
@RequiredArgsConstructor
public class UserAssignedListener {
    private final UserService userService;
    private final CategoryService categoryService;

    @RabbitHandler
    public void handleAssigned(Map<String, Object> payload){
        Long checkerId = (Long) payload.get("checkerId");
        List<Long> attendantsIds = Arrays.asList((Long[]) payload.get("attendantsIds"));
        String categoryId = (String) payload.get("categoryId");

        CategoryId category = categoryService.saveIfNotExists(categoryId);

        List<Long> usersIds = Stream
                .concat(Stream.of(checkerId), attendantsIds.stream())
                .collect(Collectors.toList());
        List<User> users = userService.findAllByIdIn(usersIds);

        users.forEach(u -> u.addCategory(category));

        userService.saveAll(users);
    }
}
