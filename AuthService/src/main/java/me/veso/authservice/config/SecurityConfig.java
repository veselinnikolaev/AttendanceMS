package me.veso.authservice.config;

import lombok.RequiredArgsConstructor;
import me.veso.authservice.client.UserClient;
import me.veso.authservice.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserClient userClient;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login", "lb://USER_SERVICE/users/register", "lb://SERVICE_REGISTRY/eureka").permitAll()
                        .requestMatchers("lb://USER_SERVICE/users/**", "lb://CATEGORY_SERVICE/categories/**", "lb://ATTENDANCE_SERVICE/attendance/**").authenticated()
                        .requestMatchers("lb://USER_SERVICE/users/**", "lb://CATEGORY_SERVICE/categories/**", "lb://ATTENDANCE_SERVICE/attendance/**", "/auth/**").hasRole("admin")
                        .requestMatchers("lb://CATEGORY_SERVICE/categories/*/assign", "lb://ATTENDANCE_SERVICE/attendance/user/{userId}", "lb://ATTENDANCE_SERVICE/attendance/category/{categoryId}").hasRole("checker")
                        .requestMatchers("lb://ATTENDANCE_SERVICE/attendance/user/{userId}", "lb://ATTENDANCE_SERVICE/attendance/category/{categoryId}").hasRole("attendant")
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .build();
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService(userClient);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(encoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
