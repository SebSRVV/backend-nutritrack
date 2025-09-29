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
    private final String serviceKey; // opcional por si quieres fallback admin

    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.anonKey}") String anonKey,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.anonKey = anonKey;
        this.serviceKey = serviceKey;

        this.rest = builder
                .baseUrl(baseUrl + "/rest/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey) // 👈 SIEMPRE anonKey
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    // ------- helpers CRUD que ya tenías (sin cambios sustanciales) -------

    public Mono<List> insert(String table, Map<String,Object> row) {
        return rest.post().uri("/" + table).bodyValue(List.of(row)).retrieve().bodyToMono(List.class);
    }

    public Mono<List> select(String table, String queryParams) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get().uri("/" + table + qp).retrieve().bodyToMono(List.class);
    }

    public Mono<Integer> delete(String table, String queryParams) {
        return rest.delete().uri("/" + table + "?" + queryParams)
                .retrieve().toBodilessEntity().map(resp -> resp.getStatusCode().value());
    }

    public Mono<List> upsert(String table, Map<String,Object> row) {
        return rest.post().uri("/" + table)
                .header("Prefer","resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row)).retrieve().bodyToMono(List.class);
    }

    // ------- RPC con token dinámico -------

    /** Llama un RPC con el Authorization que le pases (JWT usuario o service_role) */
    public <T> Mono<T> callRpc(String fnName,
                               Map<String, Object> payload,
                               String authorizationBearer, // ej. "Bearer eyJ..."
                               ParameterizedTypeReference<T> typeRef) {
        return rest.post()
                .uri("/rpc/" + fnName)
                .header(HttpHeaders.AUTHORIZATION, authorizationBearer)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(typeRef);
    }

    /** (Opcional) Fallback admin usando service_role, por si quieres modo admin */
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
