package me.veso.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.veso.authservice.dto.TokenResponse;
import me.veso.authservice.dto.UserLoginDto;
import me.veso.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @GetMapping("/validate")
    @Validated
    public ResponseEntity<?> validateToken(
            @RequestParam("token") @NotBlank(message = "Token cannot be blank") String token) {
        return service.validateToken(token)
                ? ResponseEntity.ok(new TokenResponse(true, service.extractUsernameFromToken(token), service.extractRoleFromToken(token)))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse(false, null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto userLoginDto){
        return ResponseEntity.ok(service.login(userLoginDto));
    }

    @PostMapping("/logout")
    @Validated
    public ResponseEntity<String> logout(@RequestHeader("Authorization") @NotBlank(message = "Authorization header is required") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long expiration = service.getTokenExpirationInSeconds(token);
            service.blacklistToken(token, expiration);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/blacklist/check")
    @Validated
    public ResponseEntity<Boolean> isBlacklisted(@RequestParam @NotBlank(message = "Token param is required") String token) {
        return ResponseEntity.ok(service.isTokenBlacklisted(token));
    }
}
