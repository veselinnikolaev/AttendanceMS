package me.veso.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserDetailsDto register(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        List<User> users = userRepository.findAll();
        if (users
                .stream().anyMatch(user -> user.getUsername().equals(userRegisterDto.getUsername()))) {
            throw new RuntimeException("Username already exists");
        }
        if (users.stream()
                .anyMatch(user -> user.getEmail().equals(userRegisterDto.getEmail()))) {
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
        return new UserStatusDto(id, status);
    }

    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDetailsDto::new)
                .toList();
    }

    public UserDetailsDto getUser(Long id) {
        return new UserDetailsDto(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " found")));
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
}
