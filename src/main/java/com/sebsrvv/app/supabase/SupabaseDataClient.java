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

    private final WebClient rest;
    private final String anonKey;
    private final String serviceKey;

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

    /** Añade Authorization solo si no viene null/blank. */
    private <T extends WebClient.RequestHeadersSpec<?>> T withAuth(T spec, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            spec.header(HttpHeaders.AUTHORIZATION, authorization);
        }
        return spec;
    }

    // ---------- REST PATCH con/sin AUTH ----------
    public Mono<List<Map<String, Object>>> patch(String table,
                                                 String queryParams,
                                                 Map<String, Object> fields,
                                                 String authorizationBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return withAuth(rest.patch().uri("/" + table + qp), authorizationBearer)
                .bodyValue(fields)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    /** Overload sin Authorization (retro-compatible). */
    public Mono<List<Map<String, Object>>> patch(String table,
                                                 String queryParams,
                                                 Map<String, Object> fields) {
        return patch(table, queryParams, fields, null);
    }

    // ---------- REST DELETE con/sin AUTH ----------
    public Mono<Integer> delete(String table,
                                String queryParams,
                                String authorizationBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return withAuth(rest.delete().uri("/" + table + qp), authorizationBearer)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /** Overload sin Authorization (retro-compatible). */
    public Mono<Integer> delete(String table, String queryParams) {
        return delete(table, queryParams, null);
    }

    // ---------- REST INSERT/UPSERT/SELECT con/sin AUTH ----------
    public Mono<List<Map<String, Object>>> insert(String table,
                                                  Map<String, Object> row,
                                                  String authorizationBearer) {
        return withAuth(rest.post().uri("/" + table), authorizationBearer)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public Mono<List<Map<String, Object>>> insert(String table, Map<String, Object> row) {
        return insert(table, row, null);
    }

    public Mono<List<Map<String, Object>>> upsert(String table,
                                                  Map<String, Object> row,
                                                  String authorizationBearer) {
        return withAuth(rest.post().uri("/" + table), authorizationBearer)
                .header("Prefer", "resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public Mono<List<Map<String, Object>>> upsert(String table, Map<String, Object> row) {
        return upsert(table, row, null);
    }

    public Mono<List<Map<String, Object>>> select(String table,
                                                  String queryParams,
                                                  String authorizationBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return withAuth(rest.get().uri("/" + table + qp), authorizationBearer)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    public Mono<List<Map<String, Object>>> select(String table, String queryParams) {
        return select(table, queryParams, null);
    }

    // ---------- RPC (sobre cargas completas y SIN duplicados) ----------
    /** RPC con Authorization y ParameterizedTypeReference. */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               String authorizationBearer,
                               ParameterizedTypeReference<T> typeRef) {
        return withAuth(rest.post().uri("/rpc/" + fnName), authorizationBearer)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef);
    }

    /** RPC SIN Authorization con ParameterizedTypeReference (usa apikey anónima). */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef);
    }

    /** RPC con Authorization y Class<T>. */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               String authorizationBearer,
                               Class<T> clazz) {
        return withAuth(rest.post().uri("/rpc/" + fnName), authorizationBearer)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(clazz);
    }

    /** RPC SIN Authorization y Class<T>. */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               Class<T> clazz) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(clazz);
    }

    /** RPC como service_role con ParameterizedTypeReference. */
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

    /** RPC como service_role con Class<T>. */
    public <T> Mono<T> callRpcAsServiceRole(String fnName,
                                            Map<String, Object> payload,
                                            Class<T> clazz) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(clazz);
    }
}
