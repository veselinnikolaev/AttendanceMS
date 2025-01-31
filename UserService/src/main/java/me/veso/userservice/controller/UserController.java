package me.veso.userservice.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.dto.UserStatusDto;
import me.veso.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDto> register(
            @Valid @RequestBody UserRegisterDto userRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userRegisterDto));
    }

    @PutMapping("/{id}/{status}")
    @Validated
    public ResponseEntity<UserStatusDto> updateStatus(
            @Positive(message = "User id must be positive") @PathVariable("id") Long id,
            @Pattern(regexp = "^(approved|denied)$", message = "Status must be either approved or denied") @PathVariable("status") String status) {
        //TODO: Notify for status update
        return ResponseEntity.ok(userService.validateRegistration(id, status));
    }

    @GetMapping
    @Validated
    public ResponseEntity<List<UserDetailsDto>> getAllUsers(
            @Nullable
            @Pattern(regexp = "^(approved|denied|pending)$", message = "Status must be either approved, denied or pending")
            @RequestParam(value = "status", required = false) String status) {
        List<UserDetailsDto> users = (status == null)
                ? userService.getAllUsers()
                : userService.getAllUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Validated
    public ResponseEntity<UserDetailsDto> getUser(
            @Positive(message = "User id must be positive") @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/{username}")
    @Validated
    public ResponseEntity<UserDetailsDto> getUserByUsername(
            @NotBlank(message = "Username must not be blank") @PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
