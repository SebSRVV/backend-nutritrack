package com.sebsrvv.app.modules.auth.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class SupabaseAuthClient {

    private final RestClient http;
    private final String anonKey;

    public SupabaseAuthClient(
            @Value("${supabase.url}") String baseUrl,
            @Value("${supabase.anon-key}") String anonKey
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("propiedad supabase.url faltante o vacia");
        }
        if (anonKey == null || anonKey.isBlank()) {
            throw new IllegalArgumentException("propiedad supabase.anon-key faltante o vacia");
        }
        this.anonKey = anonKey;
        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", anonKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .build();
    }

    // signup email/password
    public Map<String, Object> signup(String email, String password, Map<String, Object> userMetadata) {
        Map<String, Object> body = Map.of(
                "email", email,
                "password", password,
                "data", userMetadata
        );
        return http.post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    // login password grant
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> body = Map.of("email", email, "password", password);
        return http.post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}
