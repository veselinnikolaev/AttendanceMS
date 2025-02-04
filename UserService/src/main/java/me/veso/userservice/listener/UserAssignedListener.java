package me.veso.userservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.dto.UsersAssignedEvent;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.service.CategoryService;
import me.veso.userservice.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RabbitListener(queues = "users.assigning.queue")
@RequiredArgsConstructor
@Slf4j
public class UserAssignedListener {
    private final UserService userService;
    private final CategoryService categoryService;

    @RabbitHandler
    public void handleAssigned(UsersAssignedEvent usersAssignedEvent) {
        log.debug("Received UsersAssignedEvent: {}", usersAssignedEvent);

        if (usersAssignedEvent == null || usersAssignedEvent.categoryId() == null) {
            log.warn("Received null or invalid UsersAssignedEvent: {}", usersAssignedEvent);
            return;
        }

        Long checkerId = usersAssignedEvent.checkerId();
        List<Long> attendantsIds = usersAssignedEvent.attendantsIds();
        String categoryId = usersAssignedEvent.categoryId();

        if (checkerId == null || attendantsIds == null) {
            log.warn("Invalid event data - Checker ID or Attendants list is null.");
            return;
        }

        if (attendantsIds.isEmpty()) {
            log.info("No attendants provided. Assigning only the checker (ID: {}) to category {}", checkerId, categoryId);
        }

        log.debug("Assigning users to category {}: Checker ID = {}, Attendants = {}", categoryId, checkerId, attendantsIds);

        CategoryId category = categoryService.saveIfNotExists(categoryId);

        List<Long> usersIds = Stream.concat(Stream.of(checkerId), attendantsIds.stream()).collect(Collectors.toList());
        List<User> users = userService.findAllByIdIn(usersIds);

        if (users.isEmpty()) {
            log.warn("No users found for provided IDs: {}", usersIds);
            return;
        }

        users.forEach(u -> u.addCategory(category));

        userService.saveAll(users);

        log.info("Successfully assigned {} users to category {}", users.size(), categoryId);
    }
}
