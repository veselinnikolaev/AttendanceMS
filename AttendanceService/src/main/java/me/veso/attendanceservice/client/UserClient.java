package me.veso.attendanceservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public String getStatusForId(Long id) {
        return restTemplate.getForEntity(userServiceUrl + "/{id}/status", String.class, id).getBody();
    }
}
