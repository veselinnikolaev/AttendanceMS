package me.veso.categoryservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.categoryservice.entity.UserId;
import me.veso.categoryservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;

    public UserId saveIdLongIfNotExists(Long id) {
        //TODO: is user approved
        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }

    public List<UserId> saveIdsLongIfNotExist(List<Long> ids) {
        //TODO: are users approved
        return ids.stream()
                .map(id -> userIdRepository.findByUserId(id)
                        .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id))))
                .toList();
    }
}
