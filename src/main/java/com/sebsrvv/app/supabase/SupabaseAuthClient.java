package com.sebsrvv.app.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class SupabaseAuthClient {

    private final WebClient.Builder builder;
    private final String baseUrl;
    private final String serviceKey;
    private final String anonKey;

    public SupabaseAuthClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.serviceKey}") String serviceKey,
                              @Value("${supabase.anonKey}") String anonKey) {
        this.builder = builder;
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.anonKey = anonKey;
    }

    /** -------------------- ADMIN (service role) -------------------- */

    public Mono<Map> adminCreateUser(String email, String password, Map<String,Object> userMeta, boolean emailConfirm) {
        var body = Map.of(
                "email", email,
                "password", password,
                "email_confirm", emailConfirm,
                "user_metadata", userMeta
        );

        var adminClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .build();

        return adminClient.post()
                .uri("/auth/v1/admin/users")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class);
    }

    /** -------------------- USUARIO (anon key) -------------------- */

    // Login (password grant) -> Authorization: Bearer <anonKey>, apikey: <anonKey>
    public Mono<Map> passwordGrant(String email, String password) {
        var body = Map.of("email", email, "password", password);

        var publicClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .build();

        return publicClient.post()
                .uri("/auth/v1/token?grant_type=password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class);
    }

    // Refresh -> Authorization: Bearer <anonKey>, apikey: <anonKey>
    public Mono<Map> refresh(String refreshToken) {
        var body = Map.of("refresh_token", refreshToken);

        var publicClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .build();

        return publicClient.post()
                .uri("/auth/v1/token?grant_type=refresh_token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class);
    }

    // Me -> Authorization: Bearer <accessToken del usuario>, apikey: <anonKey>
    public Mono<Map> getUser(String accessToken) {
        var publicClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .build();

        return publicClient.get()
                .uri("/auth/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
