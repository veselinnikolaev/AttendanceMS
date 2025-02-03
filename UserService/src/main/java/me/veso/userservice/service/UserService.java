package me.veso.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.config.MessageQueueConfig;
import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.dto.UserStatusDto;
import me.veso.userservice.entity.User;
import me.veso.userservice.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RabbitTemplate rabbitTemplate;

    public UserDetailsDto register(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User()
                .setUsername(userRegisterDto.getUsername())
                .setEmail(userRegisterDto.getEmail())
                .setPasswordHash(encoder.encode(userRegisterDto.getPassword()))
                .setRole(userRegisterDto.getRole())
                .setStatus("pending");

        return new UserDetailsDto(userRepository.save(user));
    }

    @Transactional
    public UserStatusDto validateRegistration(Long id, String status) {
        userRepository.updateStatus(id, status, LocalDateTime.now());

        UserStatusDto userStatusDto = new UserStatusDto(id, status);

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "user.status.updated", userStatusDto);

        return userStatusDto;
    }

    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDetailsDto::new)
                .toList();
    }

    public UserDetailsDto getUser(Long id) {
        return new UserDetailsDto(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found")));
    }

    public List<UserDetailsDto> getAllUsersByStatus(String status) {
        return userRepository.findAllByStatus(status)
                .stream()
                .map(UserDetailsDto::new)
                .collect(Collectors.toList());
    }

    public UserDetailsDto getUserByUsername(String username) {
        return new UserDetailsDto(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username + " not found")));
    }

    public String getStatusById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"))
                .getStatus();
    }

    public List<String> getStatusesByIds(List<Long> ids) {
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(User::getStatus)
                .collect(Collectors.toList());
    }

    public List<UserDetailsDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(UserDetailsDto::new)
                .collect(Collectors.toList());
    }

    public List<User> findAllByIdIn(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds);
    }

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    public List<User> findAllByCategoryId(String id) {
        return userRepository.findAllByCategoryId(id);
    }
}
