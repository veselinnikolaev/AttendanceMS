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
import org.springframework.cache.annotation.CacheEvict;
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
    private final UserService self;

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
        return userMapper.toUserDetailsDto(self.saveUser(user));
    }

    public UserStatusDto validateRegistration(Long id, String status) {
        log.debug("Validating user with ID {} with status {}", id, status);

        self.updateStatusOfUser(status, id);

        UserStatusDto userStatusDto = new UserStatusDto(id, status);
        rabbitClient.notifyStatusUpdated(userStatusDto);

        log.info("User ID {} validation completed. New status: {}", id, status);
        return userStatusDto;
    }

    public List<UserDetailsDto> getAllUsersDetails() {
        log.debug("Fetching all users");
        return self.getAllUsers()
                .stream()
                .map(userMapper::toUserDetailsDto)
                .toList();
    }

    public UserDetailsDto getUserDetailsById(Long id) {
        log.debug("Fetching user with ID {}", id);
        User user = self.getUserById(id);
        return userMapper.toUserDetailsDto(user);
    }

    public List<UserDetailsDto> getAllUsersByStatus(String status) {
        log.debug("Fetching all users with status: {}", status);
        return self.getUsersByStatus(status)
                .stream()
                .map(userMapper::toUserDetailsDto)
                .collect(Collectors.toList());
    }

    public UserDetailsDto getUserDetailsByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = self.getUserByUsername(username);
        return userMapper.toUserDetailsDto(user);
    }

    public String getStatusById(Long id) {
        log.debug("Fetching status for user with ID {}", id);
        return self.getUserById(id)
                .getStatus();
    }

    public List<String> getStatusesByIds(List<Long> ids) {
        log.debug("Fetching statuses for user IDs: {}", ids);
        return self.findAllByIdIn(ids)
                .stream()
                .map(User::getStatus)
                .collect(Collectors.toList());
    }

    public List<UserDetailsDto> getUsersByIds(List<Long> ids) {
        log.debug("Fetching users by IDs: {}", ids);
        return self.findAllByIdIn(ids)
                .stream()
                .map(userMapper::toUserDetailsDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatusOfUser(String status, Long userId) {
        userRepository.updateStatus(userId, status, LocalDateTime.now());
        self.updateUserCache();
    }

    @Transactional
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        self.updateUserCache();
        return savedUser;
    }

    @Transactional
    public void saveAll(List<User> users) {
        log.debug("Saving list of users. Count: {}", users.size());
        userRepository.saveAll(users);
        log.info("Successfully saved {} users", users.size());
        self.updateUserCache();
    }

    @Cacheable(value = "usersByIds", key = "#userIds.hashCode()")
    public List<User> findAllByIdIn(List<Long> userIds) {
        log.debug("Fetching all users by ID list: {}", userIds);
        return userRepository.findAllByIdIn(userIds);
    }

    @Cacheable(value = "usersByCategory", key = "#id")
    public List<User> findAllByCategoryId(String id) {
        log.debug("Fetching all users by category ID: {}", id);
        return userRepository.findAllByCategoryId(id);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> getAllUsers() {
        log.debug("Fetching all users from the database (cache miss).");
        return userRepository.findAll();
    }

    @Cacheable(value = "usersById", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("User with ID {} not found", id);
            return new RuntimeException("User with ID " + id + " not found");
        });
    }

    @Cacheable(value = "usersByUsername", key = "#username")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User with username {} not found", username);
            return new RuntimeException("User with username " + username + " not found");
        });
    }

    @Cacheable(value = "usersByStatus", key = "#status")
    public List<User> getUsersByStatus(String status) {
        return userRepository.findAllByStatus(status);
    }

    @CacheEvict(value = {"users", "usersByIds", "usersByCategory", "usersById", "usersByUsername"}, allEntries = true)
    public void updateUserCache() {
        log.info("Evicting cache for users");
    }
}
