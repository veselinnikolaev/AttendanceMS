package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.config.MessageQueueConfig;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";
    private final RabbitTemplate rabbitTemplate;


    public UserId saveIdLongIfNotExists(Long id) {
        String status = client.getForObject(userServiceUrl + "/{id}/status", String.class, id);
        if(!"approved".equals(status)){
            throw new RuntimeException("User is not approved, actual status " + status);
        }

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned", id);

        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }

    public List<UserId> saveIdsLongIfNotExist(List<Long> ids) {
        String[] statuses = client.postForObject(userServiceUrl + "/status", ids, String[].class);

        Map<Long, String> idStatusMap = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(ids::get, i -> statuses[i]));

        List<Long> approvedIds = ids.stream()
                .filter(id -> "approved".equals(idStatusMap.get(id)))
                .toList();

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "users.assigned", approvedIds);

        List<UserId> existingUserIds = userIdRepository.findAllByUserIdIn(approvedIds);

        Set<Long> existingIdSet = existingUserIds.stream()
                .map(UserId::getUserId)
                .collect(Collectors.toSet());

        List<UserId> newUserIds = approvedIds.stream()
                .filter(id -> !existingIdSet.contains(id))
                .map(id -> new UserId().setUserId(id))
                .toList();

        if (!newUserIds.isEmpty()) {
            userIdRepository.saveAll(newUserIds);
        }

        return Stream.concat(existingUserIds.stream(), newUserIds.stream())
                .toList();
    }
}
