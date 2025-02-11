package me.veso.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.client.RabbitClient;
import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.dto.UserStatusDto;
import me.veso.userservice.entity.User;
import me.veso.userservice.mapper.UserMapper;
import me.veso.userservice.repository.UserRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final UserMapper userMapper;
    private final CacheManager cacheManager;

    @CachePut(value = "users", key = "#result.id()")
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
        return userMapper.toUserDetailsDto(userRepository.save(user));
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

    @Cacheable(value = "users", key = "'all'")
    public List<UserDetailsDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDetailsDto)
                .toList();
    }

    @Cacheable(value = "users", key = "#id")
    public UserDetailsDto getUser(Long id) {
        log.debug("Fetching user with ID {}", id);
        return userMapper.toUserDetailsDto(userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new RuntimeException("User with ID " + id + " not found");
                }));
    }

    @Cacheable(value = "usersByStatus", key = "#status")
    public List<UserDetailsDto> getAllUsersByStatus(String status) {
        log.debug("Fetching all users with status: {}", status);
        return userRepository.findAllByStatus(status)
                .stream()
                .map(userMapper::toUserDetailsDto)
                .collect(Collectors.toList());
    }

    public UserDetailsDto getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return userMapper.toUserDetailsDto(userRepository
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
                .map(userMapper::toUserDetailsDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "users", key = "#userIds")
    public List<User> findAllByIdIn(List<Long> userIds) {
        log.debug("Fetching all users by ID list: {}", userIds);
        return userRepository.findAllByIdIn(userIds);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void saveAll(List<User> users) {
        log.debug("Saving list of users. Count: {}", users.size());
        userRepository.saveAll(users);
        log.info("Successfully saved {} users", users.size());
    }

    @Cacheable(value = "usersByCategory", key = "#id")
    public List<User> findAllByCategoryId(String id) {
        log.debug("Fetching all users by category ID: {}", id);
        return userRepository.findAllByCategoryId(id);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> getAll(){
        return userRepository.findAll();
    }
}
