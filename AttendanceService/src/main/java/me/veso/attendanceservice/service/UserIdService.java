package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.UserClient;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final UserClient client;

    public UserId saveIdLongIfNotExists(Long id) {
        CompletableFuture
                .supplyAsync(() -> client.getStatusForId(id))
                .exceptionally(ex -> {
                    log.error("Failed to fetch user status for ID {}: {}", id, ex.getMessage());
                    return null;
                }).thenAccept(status -> {
                    if (!"approved".equals(status)) {
                        log.warn("User is not approved, status {}", status);
                        throw new RuntimeException("User is not approved, actual status " + status);
                    }
                }).join();

        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }
}
