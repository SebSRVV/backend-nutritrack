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
                .csrf(csrf -> csrf.disable())        // desactivar CSRF (API stateless)
                .cors(Customizer.withDefaults())     // 👈 habilitar CORS usando tu CorsConfig
                .authorizeHttpRequests(auth -> auth
                        // Preflight debe permitirse, si no el navegador nunca llega al POST real
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Rutas públicas
                        .requestMatchers("/api/auth/register", "/actuator/health").permitAll()
                        // Todo lo demás requiere JWT válido
                        .anyRequest().authenticated()
                )
                // Configura resource server con JWT (ya tienes jwk-set-uri en application.properties)
                .oauth2ResourceServer(o -> o.jwt());

        return http.build();
    }
}
