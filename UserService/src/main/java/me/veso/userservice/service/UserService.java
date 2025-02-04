package me.veso.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.RabbitClient;
import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.dto.UserStatusDto;
import me.veso.userservice.entity.User;
import me.veso.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RabbitClient rabbitClient;

    public UserDetailsDto register(UserRegisterDto userRegisterDto) {
        log.debug("Attempting to register user: {}", userRegisterDto.username());

        if (!userRegisterDto.password().equals(userRegisterDto.confirmPassword())) {
            log.warn("Passwords do not match for user: {}", userRegisterDto.username());
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByUsername(userRegisterDto.username())) {
            log.warn("Username already exists: {}", userRegisterDto.username());
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterDto.email())) {
            log.warn("Email already exists: {}", userRegisterDto.email());
            throw new RuntimeException("Email already exists");
        }

        User user = new User()
                .setUsername(userRegisterDto.username())
                .setEmail(userRegisterDto.email())
                .setPasswordHash(encoder.encode(userRegisterDto.password()))
                .setRole(userRegisterDto.role())
                .setStatus("pending");

        log.info("New user registered: {}", userRegisterDto.username());
        return new UserDetailsDto(userRepository.save(user));
    }

    @Transactional
    public UserStatusDto validateRegistration(Long id, String status) {
        log.debug("Validating user with ID {} with status {}", id, status);

        userRepository.updateStatus(id, status, LocalDateTime.now());

        UserStatusDto userStatusDto = new UserStatusDto(id, status);
        rabbitClient.notifyStatusUpdated(userStatusDto);

        log.info("User ID {} validation completed. New status: {}", id, status);
        return userStatusDto;
    }

    public List<UserDetailsDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserDetailsDto::new)
                .toList();
    }

    public UserDetailsDto getUser(Long id) {
        log.debug("Fetching user with ID {}", id);
        return new UserDetailsDto(userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new RuntimeException("User with ID " + id + " not found");
                }));
    }

    public List<UserDetailsDto> getAllUsersByStatus(String status) {
        log.debug("Fetching all users with status: {}", status);
        return userRepository.findAllByStatus(status)
                .stream()
                .map(UserDetailsDto::new)
                .collect(Collectors.toList());
    }

    public UserDetailsDto getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return new UserDetailsDto(userRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with username {} not found", username);
                    return new RuntimeException("User with username " + username + " not found");
                }));
    }

    public String getStatusById(Long id) {
        log.debug("Fetching status for user with ID {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new RuntimeException("User with ID " + id + " not found");
                })
                .getStatus();
    }

    public List<String> getStatusesByIds(List<Long> ids) {
        log.debug("Fetching statuses for user IDs: {}", ids);
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(User::getStatus)
                .collect(Collectors.toList());
    }

    public List<UserDetailsDto> getUsersByIds(List<Long> ids) {
        log.debug("Fetching users by IDs: {}", ids);
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(UserDetailsDto::new)
                .collect(Collectors.toList());
    }

    public List<User> findAllByIdIn(List<Long> userIds) {
        log.debug("Fetching all users by ID list: {}", userIds);
        return userRepository.findAllByIdIn(userIds);
    }

    public void saveAll(List<User> users) {
        log.debug("Saving list of users. Count: {}", users.size());
        userRepository.saveAll(users);
        log.info("Successfully saved {} users", users.size());
    }

    public List<User> findAllByCategoryId(String id) {
        log.debug("Fetching all users by category ID: {}", id);
        return userRepository.findAllByCategoryId(id);
    }
}
