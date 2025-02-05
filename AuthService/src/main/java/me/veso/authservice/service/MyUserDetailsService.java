package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.authservice.client.UserClient;
import me.veso.authservice.dto.UserDetailsDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    private final UserClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsDto userDetailsDto = CompletableFuture.supplyAsync(() -> client.getUserByUsername(username))
                .thenApply(user -> {
                    if (user == null) {
                        log.error("User not found with username: {}", username);
                        throw new UsernameNotFoundException("User not found with username: " + username);
                    }

                    if (!"approved".equalsIgnoreCase(user.getStatus())) {
                        log.error("User is not approved for login: {}", username);
                        throw new UsernameNotFoundException("User is not approved for login: " + username);
                    }
                    return user;
                }).exceptionally(ex -> {
                    log.error("Failed to fetch used details for user: {}", username);
                    return null;
                }).join();

        return new User(
                userDetailsDto.getUsername(),
                userDetailsDto.getPasswordHash(),
                Stream.of(userDetailsDto.getRole())
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
        );
    }
}
