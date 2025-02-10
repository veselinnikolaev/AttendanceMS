package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.authservice.client.UserClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    private final UserClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadUserByUsernameAsync(username).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to fetch user details for username: {}", username, e);
            throw new UsernameNotFoundException("Failed to load user: " + e.getMessage());
        }
    }

    private CompletableFuture<UserDetails> loadUserByUsernameAsync(String username) {
        return CompletableFuture.supplyAsync(() -> client.getUserByUsername(username))
                .thenApply(user -> {
                    if (user == null) {
                        throw new UsernameNotFoundException("User not found with username: " + username);
                    }
                    if (!"approved".equalsIgnoreCase(user.status())) {
                        throw new UsernameNotFoundException("User is not approved for login: " + username);
                    }
                    return new User(user.username(), user.passwordHash(),
                            Stream.of(user.role())
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toList()));
                });
    }
}
