package me.veso.notificationservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.notificationservice.dto.UserDetailsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public UserDetailsDto getUserForId(Long id){
        return restTemplate.getForEntity(userServiceUrl + "/{id}", UserDetailsDto.class, id).getBody();
    }

    public ResponseEntity<UserDetailsDto[]> getUsersForIds(List<Long> usersIds) {
        return restTemplate.postForEntity(userServiceUrl, usersIds, UserDetailsDto[].class);
    }
}
