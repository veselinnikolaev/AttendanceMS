package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.categoryservice.client.UserClient;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final UserClient client;
    private final UserIdService self;

    public UserId saveIdLongIfNotExists(Long id) {
        try {
            return CompletableFuture.supplyAsync(() -> client.getStatusForId(id))
                    .thenCompose(status -> {
                        if (!"approved".equals(status)) {
                            log.warn("User is not approved, status {}", status);
                            throw new RuntimeException("User is not approved, actual status " + status);
                        }

                        return CompletableFuture.supplyAsync(() -> self.saveIdIfNotExists(id));
                    }).exceptionally(ex -> {
                        log.error("Failed to fetch user status for ID {}: {}", id, ex.getMessage());
                        return null;
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserId> saveIdsLongIfNotExist(List<Long> ids) {
        log.debug("Fetching statuses for user IDs: {}", ids);

        try {
            return CompletableFuture.supplyAsync(() -> client.getStatusesForIds(ids))
                    .thenApply(statusesEntity -> {
                        if (statusesEntity.getBody() == null) {
                            log.error("Failed to fetch users with ids {}", ids);
                            throw new RuntimeException("Failed to fetch users with ids " + ids);
                        }

                        List<String> statuses = Arrays.asList(statusesEntity.getBody());

                        List<Long> approvedIds = IntStream.range(0, ids.size())
                                .filter(i -> "approved".equals(statuses.get(i)))
                                .mapToObj(ids::get)
                                .collect(Collectors.toList());

                        List<UserId> existingUserIds = self.findAllByUserIdsIn(approvedIds);
                        Set<Long> existingIdSet = existingUserIds.stream()
                                .map(UserId::getUserId)
                                .collect(Collectors.toSet());

                        List<UserId> newUserIds = approvedIds.stream()
                                .filter(id -> !existingIdSet.contains(id))
                                .map(id -> new UserId().setUserId(id))
                                .collect(Collectors.toList());

                        if (!newUserIds.isEmpty()) {
                            self.saveUserIds(newUserIds);
                        }

                        existingUserIds.addAll(newUserIds);
                        return existingUserIds;
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to fetch user statuses for IDs {}: {}", ids, ex.getMessage());
                        return new ArrayList<>();
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = "usersByUserId", key = "#id")
    public UserId saveIdIfNotExists(Long id) {
        return userIdRepository.findByUserId(id)
                .orElseGet(() -> {
                    log.info("Saving new user with id {}", id);
                    return userIdRepository.save(new UserId().setUserId(id));
                });
    }

    @Cacheable(value = "usersByUserIds", key = "#userIds.hashCode()")
    public List<UserId> findAllByUserIdsIn(List<Long> userIds){
        return userIdRepository.findAllByUserIdIn(userIds);
    }

    @Transactional
    public void saveUserIds(List<UserId> userIds) {
        userIdRepository.saveAll(userIds);
        self.evictUsersCache();
    }

    @CacheEvict(value = "usersByUserId", allEntries = true)
    public void evictUsersCache() {
        log.info("Evicting cache for users");
    }
}
