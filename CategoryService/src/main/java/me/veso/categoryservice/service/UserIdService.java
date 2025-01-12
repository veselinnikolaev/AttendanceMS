package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;

    public UserId saveIdLongIfNotExists(Long id) {
        List<Long> userIds = userIdRepository.findAll().stream().map(UserId::getUserId).toList();
        if(userIds.contains(id)) {
            return userIdRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));
        }
        UserId userId = new UserId().setUserId(id);
        return userIdRepository.save(userId);
    }

    public List<UserId> saveIdsLongIfNotExist(List<Long> ids) {
        List<Long> userIds = userIdRepository.findAll().stream().map(UserId::getUserId).toList();
        List<UserId> userIdsMapped = ids.stream().map(id -> new UserId().setUserId(id)).toList();

        List<UserId> userIdsToSave = userIdsMapped.stream()
                .filter(id -> !userIds.contains(id.getUserId()))
                .toList();
        userIdRepository.saveAll(userIdsToSave);
        return userIdsMapped;
    }
}
