// src/main/java/com/sebsrvv/app/supabase/SupabaseDataClient.java
package com.sebsrvv.app.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class SupabaseDataClient {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST_OF_MAP =
            new ParameterizedTypeReference<>() {};

    private final WebClient rest;
    private final String anonKey;
    private final String serviceKey; // opcional para llamadas como service_role

    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.anonKey}") String anonKey,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.anonKey = anonKey;
        this.serviceKey = serviceKey;

        this.rest = builder
                .baseUrl(baseUrl + "/rest/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    /* =========================================================
       CRUD "público" (sin Authorization). Útiles si no hay RLS.
       ========================================================= */

    public Mono<List<Map<String,Object>>> insert(String table, Map<String,Object> row) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> upsert(String table, Map<String,Object> row) {
        return rest.post()
                .uri("/" + table)
                .header("Prefer","resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> patch(String table, String queryParams, Map<String,Object> fields) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.patch()
                .uri("/" + table + qp)
                .bodyValue(fields)
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> select(String table, String queryParams) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<Integer> delete(String table, String queryParams) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /* =========================================================
       CRUD con Authorization (necesario con RLS).
       Pasa "Bearer <jwt>" en authorizationBearer.
       ========================================================= */

    public Mono<List<Map<String,Object>>> insertAuth(String table, Map<String,Object> row, String authorizationBearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> upsertAuth(String table, Map<String,Object> row, String authorizationBearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .header("Prefer","resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> patchAuth(String table, String queryParams, Map<String,Object> fields, String authorizationBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.patch()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(fields)
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<List<Map<String,Object>>> selectAuth(String table, String queryParams, String authorizationBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .retrieve()
                .bodyToMono(LIST_OF_MAP);
    }

    public Mono<Integer> deleteAuth(String table, String queryParams, String authorizationBearer) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /* =========================================================
       RPC
       ========================================================= */

    /** Llama un RPC con el Authorization que le pases (ej. "Bearer eyJ..."). */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               String authorizationBearer,
                               ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef);
    }

    /** Fallback admin usando service_role. */
    public <T> Mono<T> callRpcAsServiceRole(String fnName,
                                            Map<String, Object> payload,
                                            ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef);
    }
}
