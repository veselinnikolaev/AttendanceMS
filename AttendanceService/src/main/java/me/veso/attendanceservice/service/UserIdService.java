package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.UserClient;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

                        return CompletableFuture.supplyAsync(() -> self.saveByIdIfNotExists(id));
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to fetch user status for ID {}: {}", id, ex.getMessage());
                        return null;
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = "usersByUserId", key = "#id")
    public UserId saveByIdIfNotExists(Long id){
        return userIdRepository.findByUserId(id)
                .orElseGet(() -> self.saveUserById(id));
    }

    @Transactional
    public UserId saveUserById(Long id){
        UserId saveUser = userIdRepository.save(new UserId().setUserId(id));
        return self.pushUserInCache(saveUser);
    }

    @CachePut(value = "usersByUserId", key = "#userId.userId")
    public UserId pushUserInCache(UserId userId){
        log.info("User with id {} pushed into cache", userId.getUserId());
        return userId;
    }
}
