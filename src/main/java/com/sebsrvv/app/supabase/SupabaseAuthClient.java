// supabase/SupabaseAuthClient.java
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
    private final WebClient client;

    public SupabaseAuthClient(@Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .build();
    }

    public Mono<Map> adminCreateUser(String email, String password, Map<String,Object> userMeta, boolean emailConfirm) {
        var body = Map.of(
                "email", email,
                "password", password,
                "email_confirm", emailConfirm,
                "user_metadata", userMeta
        );
        return client.post().uri("/auth/v1/admin/users")
                .bodyValue(body).retrieve().bodyToMono(Map.class);
    }

    // opcional: login para emitir token desde backend
    public Mono<Map> passwordGrant(String email, String password) {
        var body = Map.of("email", email, "password", password);
        return client.post().uri("/auth/v1/token?grant_type=password")
                .bodyValue(body).retrieve().bodyToMono(Map.class);
    }
}
