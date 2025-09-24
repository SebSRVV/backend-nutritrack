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
                // usa tu CorsFilter; necesario para preflight
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // permitir TODOS los preflight OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // endpoints públicos
                        .requestMatchers("/api/auth/register", "/actuator/health").permitAll()

                        // el resto requiere JWT
                        .anyRequest().authenticated()
                )

                // antes: .oauth2ResourceServer(o -> o.jwt());  <-- deprecado
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
