package me.veso.notificationservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.notificationservice.dto.UserDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public UserDetailsDto getUserForId(Long id){
        return restTemplate.getForEntity(userServiceUrl + "/{id}", UserDetailsDto.class, id).getBody();
    }

    public List<UserDetailsDto> getUsersForIds(List<Long> usersIds) {
        return Arrays.asList(
                restTemplate.postForEntity(userServiceUrl, usersIds, UserDetailsDto[].class).getBody()
        );
    }
}
