package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public UserId saveIdLongIfNotExists(Long id) {
        String status = client.getForEntity(userServiceUrl + "/{id}", String.class, id).getBody();
        if(!"approved".equals(status)){
            throw new RuntimeException("User is not approved, actual status " + status);
        }

        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }

    public List<UserId> saveIdsLongIfNotExist(List<Long> ids) {
        String[] statuses = client.postForEntity(userServiceUrl + "/status", ids, String[].class).getBody();

        Map<Long, String> idStatusMap = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(ids::get, i -> statuses[i]));

        List<Long> approvedIds = ids.stream()
                .filter(id -> "approved".equals(idStatusMap.get(id)))
                .toList();

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
