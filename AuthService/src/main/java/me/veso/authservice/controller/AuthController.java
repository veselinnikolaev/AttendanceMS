package me.veso.authservice.controller;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.service.AuthService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @GetMapping("/validate")
    public void validateToken(@RequestParam("token") String token, UserDetails userDetails){
        service.validateToken(token, userDetails);
    }
}
