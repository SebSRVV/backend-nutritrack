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

        // Cliente base público (anonKey), útil para selects abiertos o RLS
        this.rest = builder
                .baseUrl(baseUrl + "/rest/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    // ============= Métodos CRUD genéricos (usa anonKey por defecto) =============

    public Mono<List> insert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<List> select(String table, String queryParams) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<Integer> delete(String table, String queryParams) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    public Mono<List> upsert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .header("Prefer", "resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    // ============= RPC =============

    /** Llama un RPC con el Authorization que le pases (JWT usuario o service_role) */
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

    /** Llama un RPC como service_role (admin) */
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

    // ============= Helpers adicionales =============

    public Mono<List> insertMany(String table, List<Map<String, Object>> rows) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(rows)
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<Integer> patch(String table, String queryParams, Map<String, Object> body) {
        return rest.patch()
                .uri("/" + table + "?" + queryParams)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    // ============= Versiones seguras con JWT (para RLS) =============

    public Mono<List> insertWithAuth(String table, Map<String, Object> row, String bearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<List> insertManyWithAuth(String table, List<Map<String, Object>> rows, String bearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(rows)
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<Integer> patchWithAuth(String table, String queryParams, Map<String, Object> body, String bearer) {
        return rest.patch()
                .uri("/" + table + "?" + queryParams)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /** Nuevo: select con JWT dinámico (muy útil para RLS) */
    public Mono<List> selectWithAuth(String table, String queryParams, String bearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Nuevo: delete con JWT dinámico */
    public Mono<Integer> deleteWithAuth(String table, String queryParams, String bearer) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }
}
