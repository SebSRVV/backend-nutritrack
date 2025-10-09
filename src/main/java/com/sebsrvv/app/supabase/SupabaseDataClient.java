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

/**
 * Servicio que se encarga de manejar la comunicación directa con la base de datos de Supabase
 * utilizando el cliente WebClient de Spring.
 *
 * Este componente permite ejecutar operaciones CRUD genéricas (insert, select, update, delete)
 * y también funciones RPC (Remote Procedure Calls).
 *
 * Es utilizado, por ejemplo, por el módulo "meals" para registrar, actualizar o eliminar comidas
 * mediante las tablas y funciones remotas definidas en Supabase.
 */
@Service
public class SupabaseDataClient {

    // Cliente WebClient configurado para comunicarse con Supabase REST
    private final WebClient rest;

    // Claves de acceso a Supabase (pública y de servicio)
    private final String anonKey;
    private final String serviceKey;

    /**
     * Constructor que configura el cliente HTTP base para realizar las peticiones REST hacia Supabase.
     *
     * @param builder objeto builder del WebClient
     * @param baseUrl URL base de Supabase (por ejemplo: https://xxxxx.supabase.co)
     * @param anonKey clave pública (anon)
     * @param serviceKey clave privada del rol de servicio (service_role)
     */
    public SupabaseDataClient(WebClient.Builder builder,
                              @Value("${supabase.url}") String baseUrl,
                              @Value("${supabase.anonKey}") String anonKey,
                              @Value("${supabase.serviceKey}") String serviceKey) {
        this.anonKey = anonKey;
        this.serviceKey = serviceKey;

        // Configuración del cliente base público (usa anonKey)
        // Este cliente se usa para consultas abiertas o funciones con Row-Level Security (RLS)
        this.rest = builder
                .baseUrl(baseUrl + "/rest/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", anonKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .defaultHeader("Prefer", "return=representation")
                .build();
    }

    // =========================================================================
    // ============= MÉTODOS CRUD GENÉRICOS (usa anonKey por defecto) ===========
    // =========================================================================

    /** Inserta una nueva fila en una tabla específica */
    public Mono<List> insert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Obtiene datos desde una tabla aplicando filtros opcionales */
    public Mono<List> select(String table, String queryParams) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Elimina filas de una tabla según parámetros de búsqueda */
    public Mono<Integer> delete(String table, String queryParams) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /** Inserta o actualiza registros (merge) según el contenido existente */
    public Mono<List> upsert(String table, Map<String, Object> row) {
        return rest.post()
                .uri("/" + table)
                .header("Prefer", "resolution=merge-duplicates,return=representation")
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    // =========================================================================
    // ===================== LLAMADAS A FUNCIONES RPC ==========================
    // =========================================================================

    /**
     * Llama a una función RPC personalizada de Supabase, pasando el token JWT que se indique.
     * Este métdo permite invocar funciones seguras según el rol del usuario autenticado.
     */
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

    /**
     * Llama a una función RPC con privilegios de administrador (service_role).
     * Se usa para operaciones que requieren más permisos, como mantenimiento o migraciones.
     */
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

    // =========================================================================
    // ===================== MÉTODOS AUXILIARES ================================
    // =========================================================================

    /** Inserta múltiples filas a la vez en una tabla */
    public Mono<List> insertMany(String table, List<Map<String, Object>> rows) {
        return rest.post()
                .uri("/" + table)
                .bodyValue(rows)
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Actualiza (PATCH) registros existentes según filtros */
    public Mono<Integer> patch(String table, String queryParams, Map<String, Object> body) {
        return rest.patch()
                .uri("/" + table + "?" + queryParams)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    // =========================================================================
    // ============= VERSIONES SEGURAS CON JWT (para RLS en Supabase) ==========
    // =========================================================================

    /**
     * Inserta un registro con autenticación JWT.
     * Usado por el módulo "meals" para registrar comidas bajo el usuario actual.
     */
    public Mono<List> insertWithAuth(String table, Map<String, Object> row, String bearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(List.of(row))
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Inserta múltiples registros autenticados con JWT */
    public Mono<List> insertManyWithAuth(String table, List<Map<String, Object>> rows, String bearer) {
        return rest.post()
                .uri("/" + table)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(rows)
                .retrieve()
                .bodyToMono(List.class);
    }

    /** Actualiza registros autenticados (solo si pertenecen al usuario) */
    public Mono<Integer> patchWithAuth(String table, String queryParams, Map<String, Object> body, String bearer) {
        return rest.patch()
                .uri("/" + table + "?" + queryParams)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }

    /**
     * Realiza una consulta segura usando el token JWT del usuario.
     * Muy útil para aplicar las reglas de seguridad RLS de Supabase.
     * En el módulo "meals", se usa para obtener comidas específicas del usuario autenticado.
     */
    public Mono<List> selectWithAuth(String table, String queryParams, String bearer) {
        String qp = (queryParams == null || queryParams.isBlank()) ? "" : "?" + queryParams;
        return rest.get()
                .uri("/" + table + qp)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .retrieve()
                .bodyToMono(List.class);
    }

    /**
     * Elimina registros de forma segura con autenticación JWT.
     * En el módulo "meals", se utiliza cuando un usuario elimina una comida suya.
     */
    public Mono<Integer> deleteWithAuth(String table, String queryParams, String bearer) {
        return rest.delete()
                .uri("/" + table + "?" + queryParams)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .retrieve()
                .toBodilessEntity()
                .map(resp -> resp.getStatusCode().value());
    }
}
