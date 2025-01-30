package me.veso.apigateway.config;

import lombok.RequiredArgsConstructor;
import me.veso.apigateway.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Value("${free-resources.urls}")
    private String[] freeResourceUrls;

    @Value("${authenticated-resources.urls}")
    private String[] authenticatedResourceUrls;

    @Value("${admin-resources.urls}")
    private String[] adminResourceUrls;

    @Value("${checker-resources.urls}")
    private String[] checkerResourceUrls;

    @Value("${attendant-resources.urls}")
    private String[] attendantResourceUrls;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Free resources
                        .requestMatchers(freeResourceUrls).permitAll()

                        // Admin resources
                        .requestMatchers(adminResourceUrls).hasRole("ADMIN")

                        // Checker resources
                        .requestMatchers(checkerResourceUrls).hasRole("CHECKER")

                        // Attendant resources
                        .requestMatchers(attendantResourceUrls).hasRole("ATTENDANT")

                        // Authenticated resources
                        .requestMatchers(authenticatedResourceUrls).authenticated()

                        // Deny all other requests by default
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
