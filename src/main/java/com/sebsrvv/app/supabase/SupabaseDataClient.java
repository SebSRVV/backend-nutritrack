package com.sebsrvv.app.supabase;

import org.springframework.beans.factory.annotation.Value;
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

    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.rest = builder
                .baseUrl(baseUrl + "/rest/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    public Mono<List> insert(String table, Map<String,Object> row) {
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

    public Mono<List> upsert(String table, Map<String,Object> row) {
        return rest.post()
                .uri("/" + table)
                .header("Prefer","resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }
}
