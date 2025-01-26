package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {
    private RestTemplate client;
    private JwtService jwtService;

    public String generateToken(String username){
        return jwtService.generateToken(username);
    }

    public void validateToken(String token, UserDetails userDetails){
        jwtService.validateToken(token, userDetails);
    }
}
