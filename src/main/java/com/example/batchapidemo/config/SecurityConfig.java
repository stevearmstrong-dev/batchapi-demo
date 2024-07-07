package com.example.batchapidemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Specify CSRF token repository
                        .ignoringRequestMatchers(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**"))  // Correctly ignore CSRF for /api/**
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**").permitAll()  // Permit all requests to /api/**
                        .anyRequest().authenticated()  // Require authentication for all other requests
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
