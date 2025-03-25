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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        return ResponseEntity.ok(userService.validateRegistration(id, status));
    }

    @GetMapping
    @Validated
    public ResponseEntity<?> getUsers(
            @Nullable
            @Pattern(regexp = "^(approved|denied|pending)$", message = "Status must be either approved, denied or pending")
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(value = "size", required = false, defaultValue = "10") String sizeParam) {
        ResponseEntity<?> response;
        if (!"all".equalsIgnoreCase(status)) {
            response = ResponseEntity.ok(userService.getAllUsersByStatus(status));
        } else if ("all".equalsIgnoreCase(sizeParam)) {
            response = ResponseEntity.ok(userService.getAllUsersDetails());
        } else {
            int size = Integer.parseInt(sizeParam);
            pageable = PageRequest.of(pageable.getPageNumber(), size);
            response = ResponseEntity.ok(userService.getUsersDetailsPageable(pageable));
        }
        return response;
    }

    @PostMapping
    @Validated
    public ResponseEntity<?> getUsersByIds(@RequestBody List<@Positive(message = "User id must be positive") Long> ids,
                                           @PageableDefault(page = 0, size = 10) Pageable pageable,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") String sizeParam) {
        ResponseEntity<?> response;
        if ("all".equalsIgnoreCase(sizeParam)) {
            response = ResponseEntity.ok(userService.getUsersByIds(ids));
        } else {
            int size = Integer.parseInt(sizeParam);
            pageable = PageRequest.of(pageable.getPageNumber(), size);
            response = ResponseEntity.ok(userService.getUsersDetailsByIdsPageable(ids, pageable));
        }

        return response;
    }

    @GetMapping("/{id}")
    @Validated
    public ResponseEntity<UserDetailsDto> getUser(
            @Positive(message = "User id must be positive") @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserDetailsById(id));
    }

    @GetMapping("/{username}")
    @Validated
    public ResponseEntity<UserDetailsDto> getUserByUsername(
            @NotBlank(message = "Username must not be blank") @PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserDetailsByUsername(username));
    }

    @GetMapping("/{id}/status")
    @Validated
    public ResponseEntity<String> getStatusById(@Positive(message = "User id must be positive") @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getStatusById(id));
    }

    @PostMapping("/status")
    @Validated
    public ResponseEntity<?> getStatusesByIds(@RequestBody List<@Positive(message = "User id must be positive") Long> ids,
                                                         @PageableDefault(page = 0, size = 10) Pageable pageable,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") String sizeParam) {
        ResponseEntity<?> response;
        if ("all".equalsIgnoreCase(sizeParam)) {
            response = ResponseEntity.ok(userService.getStatusesByIds(ids));
        } else {
            int size = Integer.parseInt(sizeParam);
            pageable = PageRequest.of(pageable.getPageNumber(), size);
            response = ResponseEntity.ok(userService.getStatusesByIdsPageable(ids, pageable));
        }

        return response;
    }
}
