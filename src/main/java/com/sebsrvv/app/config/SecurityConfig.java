// src/main/java/com/sebsrvv/app/config/SecurityConfig.java
package com.sebsrvv.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Preflight y estáticos (si los hubiera)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Actuator básicos
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Auth públicas
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()

                        // ✅ Endpoint PÚBLICO de métricas
                        .requestMatchers(HttpMethod.GET, "/api/metrics").permitAll()

                        // Todo lo demás requiere JWT
                        .anyRequest().authenticated()
                )
                // Resource server con JWT (para el resto)
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
