package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.notificationservice.client.CategoryClient;
import me.veso.notificationservice.client.UserClient;
import me.veso.notificationservice.dto.CategoryDetailsDto;
import me.veso.notificationservice.dto.UserDetailsDto;
import me.veso.notificationservice.dto.UsersAssignedEvent;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RabbitListener(queues = "users.assigning.queue")
@RequiredArgsConstructor
@Slf4j
public class UsersAssignedListener {
    private final MailService mailService;
    private final UserClient userClient;
    private final CategoryClient categoryClient;

    @RabbitHandler
    public void handleAssigned(UsersAssignedEvent usersAssignedEvent) {
        Long checkerId = usersAssignedEvent.checkerId();
        List<Long> attendantsIds = usersAssignedEvent.attendantsIds();
        String categoryId = usersAssignedEvent.categoryId();

        List<Long> usersIds = Stream.concat(Stream.of(checkerId), attendantsIds.stream())
                .collect(Collectors.toList());

        CompletableFuture<List<UserDetailsDto>> usersFuture = CompletableFuture.supplyAsync(() ->
                        userClient.getUsersForIds(usersIds))
                .thenApply(usersEntity -> {
                    if (usersEntity.getBody() == null) {
                        log.error("Failed to fetch users by ids {}", usersIds);
                        throw new RuntimeException("Failed to fetch users by ids " + usersIds);
                    }
                    return Arrays.asList(usersEntity.getBody());
                })
                .exceptionally(ex -> {
                    log.error("Failed to fetch user details: {}", ex.getMessage());
                    return Collections.emptyList();
                });

        CompletableFuture<CategoryDetailsDto> categoryFuture = CompletableFuture.supplyAsync(() ->
                        categoryClient.getCategoryForId(categoryId))
                .exceptionally(ex -> {
                    log.error("Failed to fetch category details: {}", ex.getMessage());
                    return new CategoryDetailsDto(categoryId, "Unknown Category", null, null);
                });

        usersFuture.thenCombine(categoryFuture, (users, category) -> {
            if (users.isEmpty()) {
                log.warn("No users found for IDs: {}", usersIds);
                return null;
            }

            users.forEach(user -> mailService.send(
                    user.email(),
                    "Assigned to Category",
                    String.format("""
                            Hi %s,
                            You have been assigned to a new category as %s!
                            Category name: %s.
                            """, user.username(), user.role(), category.name())
            ));
            return null;
        });
    }
}
