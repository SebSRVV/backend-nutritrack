package com.sebsrvv.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register","/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(o -> o.jwt()); // usa el jwk-set-uri de properties
        return http.build();
    }
}
