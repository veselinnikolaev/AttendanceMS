package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.notificationservice.dto.CategoryDetailsDto;
import me.veso.notificationservice.dto.UserDetailsDto;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RabbitListener(queues = "users.assigning.queue")
@RequiredArgsConstructor
public class UsersAssignedListener {
    private final MailService mailService;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    @RabbitHandler
    public void handleAssigned(Map<String, Object> payload) throws ExecutionException, InterruptedException {
        Long checkerId = (Long) payload.get("checkerId");
        List<Long> attendantsIds = Arrays.asList((Long[]) payload.get("attendantsIds"));
        String categoryId = (String) payload.get("categoryId");

        List<Long> usersIds = Stream
                .concat(Stream.of(checkerId), attendantsIds.stream())
                .collect(Collectors.toList());

        UserDetailsDto[] users =
                CompletableFuture.supplyAsync(() ->
                                client.postForEntity(userServiceUrl, usersIds, UserDetailsDto[].class).getBody())
                        .get();

        CategoryDetailsDto category =
                CompletableFuture.supplyAsync(() ->
                                client.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, categoryId).getBody())
                        .get();

        for (UserDetailsDto user : users) {
            mailService.send(user.email(), "Assigned to Category",
                            """
                            Hi %s,
                            You have been assigned for new category as %s!
                            Category name: %s.
                            """.formatted(user.username(), user.role(), category.name()));
        }
    }
}
