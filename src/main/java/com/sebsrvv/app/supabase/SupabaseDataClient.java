// src/main/java/com/sebsrvv/app/supabase/SupabaseDataClient.java
package com.sebsrvv.app.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class SupabaseDataClient {

    private final WebClient rest;
    private final String anonKey;
    private final String serviceKey;

    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.anonKey}") String anonKey,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.anonKey = anonKey;
        this.serviceKey = serviceKey;

        // 🔧 Evita duplicar "/rest/v1" en la URL
        String finalUrl = baseUrl.endsWith("/rest/v1") ? baseUrl : baseUrl + "/rest/v1";

        this.rest = builder
                .baseUrl(finalUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    // -------------------- MÉTODOS CRUD --------------------

    public Mono<List> insert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class)
                .onErrorResume(WebClientResponseException.class, this::handleError);
    }

    public Mono<List> select(String table, String queryParams) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .retrieve()
                .bodyToMono(List.class)
                .onErrorResume(WebClientResponseException.class, this::handleError);
    }

    public Mono<Integer> delete(String table, String queryParams) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value())
                .onErrorResume(WebClientResponseException.class, this::handleErrorCode);
    }

    public Mono<List> upsert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .header("Prefer", "resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class)
                .onErrorResume(WebClientResponseException.class, this::handleError);
    }

    // -------------------- RPC --------------------

    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               String authorizationBearer,
                               ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(WebClientResponseException.class, this::handleError);
    }

    public <T> Mono<T> callRpcAsServiceRole(String fnName,
                                            Map<String, Object> payload,
                                            ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(WebClientResponseException.class, this::handleError);
    }

    // -------------------- ERRORES --------------------

    private <T> Mono<T> handleError(WebClientResponseException ex) {
        System.err.println(" Supabase error (" + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
        return Mono.error(ex);
    }

    private <T> Mono<T> handleErrorCode(WebClientResponseException ex) {
        System.err.println(" HTTP error: " + ex.getStatusCode());
        return Mono.just((T) Integer.valueOf(ex.getStatusCode().value()));
    }
}
