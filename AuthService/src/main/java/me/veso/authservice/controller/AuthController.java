package me.veso.authservice.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.veso.authservice.dto.TokenResponse;
import me.veso.authservice.dto.UserLoginDto;
import me.veso.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestParam("token") @NotBlank(message = "Token cannot be blank") String token,
            @AuthenticationPrincipal UserDetails userDetails) {
        return service.validateToken(token, userDetails)
                ? ResponseEntity.ok(new TokenResponse(true, userDetails.getUsername(), service.getRole(userDetails)))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto){
        return ResponseEntity.ok(service.login(userLoginDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long expiration = service.getTokenExpirationInSeconds(token);
            service.blacklistToken(token, expiration);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/roles/{username}")
    public ResponseEntity<?> getRolesForUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(service.getRoleByUsername(username));
    }
}
