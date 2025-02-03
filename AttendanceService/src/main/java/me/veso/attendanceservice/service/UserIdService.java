package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserIdService {
    private final UserIdRepository userIdRepository;
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public UserId saveIdLongIfNotExists(Long id) {
        String status = client.getForEntity(userServiceUrl + "/{id}/status", String.class, id).getBody();
        if(!"approved".equals(status)){
            throw new RuntimeException("User is not approved, actual status " + status);
        }

        return userIdRepository.findByUserId(id)
                .orElseGet(() -> userIdRepository.save(new UserId().setUserId(id)));
    }
}
