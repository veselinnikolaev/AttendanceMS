package me.veso.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.veso.userservice.dto.UserDetailsDto;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.dto.UserStatusDto;
import me.veso.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    @PutMapping("/{id}/{status}")
    @ResponseStatus(HttpStatus.OK)
    public UserStatusDto updateStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return userService.validateRegistration(id, status);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDetailsDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDto getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }
}
