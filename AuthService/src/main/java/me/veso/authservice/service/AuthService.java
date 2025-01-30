package me.veso.authservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.dto.RoleResponse;
import me.veso.authservice.dto.UserLoginDto;
import me.veso.authservice.dto.UserLoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlackListService;

    public boolean validateToken(String token){
        return jwtService.validateToken(token);
    }

    public String extractUsernameFromToken(String token){
        return jwtService.extractUsername(token);
    }

    public String extractRoleFromToken(String token){
        return jwtService.extractRole(token);
    }

    public long getTokenExpirationInSeconds(String token){
        return jwtService.extractExpiration(token).getTime()/1000;
    }

    public void blacklistToken(String token, long expirationInSeconds){
        tokenBlackListService.blacklistToken(token, expirationInSeconds);
    }

    public boolean isTokenBlacklisted(String token){
        return tokenBlackListService.isTokenBlacklisted(token);
    }

    public String getRole(UserDetails userDetails){
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }

    public UserLoginResponse login(UserLoginDto userLoginDto) {
        Authentication authentication  = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDto.getUsername(),
                userLoginDto.getPassword()
        ));

        String accessToken = jwtService.generateToken(authentication.getName(), authentication.getAuthorities());
        return new UserLoginResponse(accessToken);
    }

    public RoleResponse getRoleByUsername(String username) {
        return new RoleResponse(username, getRole(userDetailsService.loadUserByUsername(username)));
    }
}
