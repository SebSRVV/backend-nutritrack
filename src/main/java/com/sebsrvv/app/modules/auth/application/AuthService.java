// modules/auth/application/AuthService.java
package com.sebsrvv.app.modules.auth.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class AuthService {

    private final WebClient http;
    private final String serviceKey;

    public AuthService(
            @Value("${supabase.url}") String baseUrl,
            @Value("${supabase.service-key}") String serviceKey
    ) {
        this.serviceKey = serviceKey;
        this.http = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();
    }

    public Mono<Map<String, Object>> register(Map<String, Object> payload) {
        String email    = (String) payload.get("email");
        String password = (String) payload.get("password");

        // --- calcular BMI en backend ---
        Integer h = (Integer) payload.get("height_cm");
        Integer w = (Integer) payload.get("weight_kg");
        Double bmi = computeBmi(h, w);  // redondeado a 1 decimal

        // --- (opcional) edad desde dob ---
        String dobStr = (String) payload.get("dob");
        Integer age = safeAgeFromDob(dobStr); // puede ser null si dob inválido

        Map<String, Object> metadata = new HashMap<>(payload);
        metadata.remove("email");
        metadata.remove("password");
        metadata.put("bmi", bmi);
        if (age != null) metadata.put("age", age);

        Map<String, Object> body = Map.of(
                "email", email,
                "password", password,
                "user_metadata", metadata,
                "email_confirm", false
        );

        return http.post()
                .uri("/auth/v1/admin/users")
                .bodyValue(body)
                .exchangeToMono(this::handleResponse);
    }

    private static Double computeBmi(Integer heightCm, Integer weightKg) {
        if (heightCm == null || weightKg == null || heightCm == 0) return null;
        double m = heightCm / 100.0;
        double raw = weightKg / (m * m);
        return Math.round(raw * 10.0) / 10.0; // 1 decimal
    }

    private static Integer safeAgeFromDob(String dob) {
        try {
            LocalDate birth = LocalDate.parse(dob);             // espera ISO: YYYY-MM-DD
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Map<String, Object>> handleResponse(ClientResponse res) {
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(Map.class).map(resp -> {
                Object maybeUser = resp.get("user");
                Map<String, Object> user =
                        (maybeUser instanceof Map) ? (Map<String, Object>) maybeUser : resp;

                if (user == null || user.get("id") == null) {
                    throw new IllegalStateException("Respuesta inesperada de Supabase (no viene user/id).");
                }
                Map<String, Object> out = new HashMap<>();
                out.put("id", user.get("id"));
                out.put("email", user.get("email"));
                out.put("user_metadata", user.get("user_metadata"));
                return out;
            });
        } else {
            return res.bodyToMono(Map.class).defaultIfEmpty(Map.of())
                    .flatMap(err -> {
                        String msg = (String) err.getOrDefault("message",
                                "Supabase error: " + res.statusCode());
                        return Mono.error(new RuntimeException(msg));
                    });
        }
    }
}
