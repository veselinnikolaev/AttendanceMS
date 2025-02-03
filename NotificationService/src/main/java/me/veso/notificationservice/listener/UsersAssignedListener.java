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
        List<Long> userIds = Arrays.asList((Long[]) payload.get("userIds"));
        String categoryId = (String) payload.get("categoryId");

        UserDetailsDto[] users =
                CompletableFuture.supplyAsync(() ->
                        client.postForEntity(userServiceUrl, userIds, UserDetailsDto[].class).getBody())
                        .get();

        CategoryDetailsDto category =
                CompletableFuture.supplyAsync(() ->
                        client.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, categoryId).getBody())
                        .get();

        for (UserDetailsDto user : users){
            mailService.send(user.getEmail(), "Assigned to Category",
                    """
                    Hi %s,
                    You have been assigned for new category!
                    Category name: %s.
                    """.formatted(user.getUsername(), category.getName()));
        }
    }
}
