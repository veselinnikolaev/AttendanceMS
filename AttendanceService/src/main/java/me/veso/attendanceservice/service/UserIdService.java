package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;

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
}
