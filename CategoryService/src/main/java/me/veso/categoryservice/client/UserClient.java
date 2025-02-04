package me.veso.categoryservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public String getStatusForId(Long id) {
        return restTemplate.getForEntity(userServiceUrl + "/{id}/status", String.class, id).getBody();
    }

    public List<String> getStatusesForIds(List<Long> ids) {
        return Arrays.asList(
                restTemplate.postForEntity(userServiceUrl + "/status", ids, String[].class).getBody()
        );
    }
}
