package me.veso.authservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.dto.UserDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    public UserDetailsDto getUserByUsername(String username){
        return restTemplate.getForObject(userServiceUrl + "/{username}", UserDetailsDto.class, username);
    }
}
