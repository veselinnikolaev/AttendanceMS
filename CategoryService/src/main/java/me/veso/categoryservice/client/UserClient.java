package me.veso.categoryservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public String getStatusForId(Long id) {
        return restTemplate.getForEntity(userServiceUrl + "/{id}/status", String.class, id).getBody();
    }

    public ResponseEntity<String[]> getStatusesForIds(List<Long> ids) {
        return restTemplate.postForEntity(userServiceUrl + "/status", ids, String[].class);
    }
}
