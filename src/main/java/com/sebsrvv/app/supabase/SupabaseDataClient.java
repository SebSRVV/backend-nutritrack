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
    private final String serviceKey; // opcional (service_role)

    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.anonKey}") String anonKey,
                              @Value("${supabase.serviceKey:}") String serviceKey) {
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
       ===============  MÉTODOS SIN AUTH (anon)  ===============
       ========================================================= */

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
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.delete()
                .uri("/" + table + qp)
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

    /* =========================================================
       ==============  MÉTODOS CON AUTH (JWT)   ================
       ========================================================= */

    public Mono<List> insertWithAuth(String table, Map<String, Object> row, String authBearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, authBearer)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<List> selectWithAuth(String table, String queryParams, String authBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, authBearer)
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<Integer> deleteWithAuth(String table, String queryParams, String authBearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.delete()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, authBearer)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /** PATCH parcial con filtro estilo PostgREST, p.ej. "id=eq.{uuid}" */
    public Mono<List> patchWithAuth(String table, String filter, Map<String, Object> row, String authBearer) {
        String qp = (filter == null || filter.isBlank()) ? "" : "?" + filter;
        return rest.patch()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, authBearer)
                .bodyValue(row)
                .retrieve()
                .bodyToMono(List.class);
    }

    public Mono<List> upsertWithAuth(String table, Map<String, Object> row, String authBearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, authBearer)
                .header("Prefer", "resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    /* =========================================================
       ====================   RPC  (AUTH)   ====================
       ========================================================= */

    /** RPC genérica tipada (recomendada) */
    public <T> Mono<T> rpcWithAuth(String fnName,
                                   Map<String, Object> payload,
                                   String authorizationBearer,
                                   ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(payload == null ? Map.of() : payload)
                .retrieve()
                .bodyToMono(typeRef);
    }

    /** RPC estándar que devuelve una lista de mapas (útil para la mayoría de casos) */
    public Mono<List<Map<String, Object>>> rpcWithAuth(String fnName,
                                                       Map<String, Object> payload,
                                                       String authorizationBearer) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(payload == null ? Map.of() : payload)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    /* =========================================================
       ==============  RPC como service_role (opcional) =========
       ========================================================= */

    public <T> Mono<T> rpcAsServiceRole(String fnName,
                                        Map<String, Object> payload,
                                        ParameterizedTypeReference<T> typeRef) {
        if (serviceKey == null || serviceKey.isBlank()) {
            return Mono.error(new IllegalStateException("serviceKey no configurado"));
        }
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(payload == null ? Map.of() : payload)
                .retrieve()
                .bodyToMono(typeRef);
    }
}
