package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.dto.UserDetailsDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final RestTemplate client;
    private final String userServiceUrl = "http://USER_SERVICE/users";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsDto userDetailsDto = client.getForObject(userServiceUrl + "/{username}", UserDetailsDto.class, username);

        if (userDetailsDto == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        if (!"approved".equalsIgnoreCase(userDetailsDto.getStatus())) {
            throw new UsernameNotFoundException("User is not approved for login: " + username);
        }

        return new User(
                userDetailsDto.getUsername(),
                userDetailsDto.getPasswordHash(),
                Stream.of(userDetailsDto.getRole())
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
        );
    }
}
