package com.sebsrvv.app.modules.auth.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebsrvv.app.modules.auth.domain.EmailAlreadyExistsException;
import com.sebsrvv.app.modules.auth.domain.InvalidCredentialsException;
import com.sebsrvv.app.modules.auth.domain.InvalidEmailException;
import com.sebsrvv.app.modules.auth.domain.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_OF_OBJECT =
            new ParameterizedTypeReference<>() {};
    private static final ObjectMapper JSON = new ObjectMapper();

    /** Cliente con serviceKey (admin) */
    private final WebClient adminHttp;
    /** Cliente con anonKey (público) */
    private final WebClient publicHttp;
    /** Necesario para el header Authorization en /token */
    private final String anonKey;

    public AuthService(
            @Value("${supabase.url}") String baseUrl,
            @Value("${supabase.serviceKey}") String serviceKey,
            @Value("${supabase.anonKey}") String anonKey
    ) {
        this.anonKey = anonKey;

        this.adminHttp = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .filter((req, next) -> {
                    log.info("[SUPABASE/ADMIN] --> {} {}", req.method(), req.url());
                    return next.exchange(req)
                            .doOnNext(res -> log.info("[SUPABASE/ADMIN] <-- {} {}",
                                    res.statusCode().value(),
                                    res.headers().asHttpHeaders().getContentType()));
                })
                .build();

        this.publicHttp = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", anonKey)
                .filter((req, next) -> {
                    log.info("[SUPABASE/PUBLIC] --> {} {}", req.method(), req.url());
                    return next.exchange(req)
                            .doOnNext(res -> log.info("[SUPABASE/PUBLIC] <-- {} {}",
                                    res.statusCode().value(),
                                    res.headers().asHttpHeaders().getContentType()));
                })
                .build();
    }

    /* ---------------------------------------------------------
     * REGISTER (admin create user)
     * --------------------------------------------------------- */
    public Mono<Map<String, Object>> register(Map<String, Object> payload) {
        final String email    = (String) payload.get("email");
        final String password = (String) payload.get("password");

        Integer h = toInt(payload.get("height_cm"));
        Integer w = toInt(payload.get("weight_kg"));
        Double bmi = computeBmi(h, w);

        Integer age = safeAgeFromDob((String) payload.get("dob"));

        Map<String, Object> userMeta = new HashMap<>(payload);
        userMeta.remove("email");
        userMeta.remove("password");
        userMeta.put("bmi", bmi);
        if (age != null) userMeta.put("age", age);

        Map<String, Object> body = Map.of(
                "email", email,
                "password", password,
                "user_metadata", userMeta,
                "email_confirm", false
        );

        log.info("[API/register] payload: {}", redact(body));

        return adminHttp.post()
                .uri("/auth/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(res -> handleRegisterResponse(res, email))
                .doOnSuccess(out -> log.info("[API/register] OK <- {}", out))
                .doOnError(err -> log.warn("[API/register] ERROR <- {}", err.getMessage()));
    }

    /* ---------------------------------------------------------
     * LOGIN (password grant) - requiere Authorization: Bearer ANON_KEY
     * --------------------------------------------------------- */
    public Mono<Map<String, Object>> login(String email, String password) {
        Map<String, Object> body = Map.of("email", email, "password", password);

        log.info("[API/login] payload: {}", redact(body));

        return publicHttp.post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)   // <-- clave
                .bodyValue(body)
                .exchangeToMono(this::handleTokenResponse)
                .doOnSuccess(out -> log.info("[API/login] OK <- keys: {}", out.keySet()))
                .doOnError(err -> log.warn("[API/login] ERROR <- {}", err.getMessage()));
    }

    /* ---------------------------------------------------------
     * REFRESH (refresh_token grant) - requiere Authorization: Bearer ANON_KEY
     * --------------------------------------------------------- */
    public Mono<Map<String, Object>> refresh(String refreshToken) {
        Map<String, Object> body = Map.of("refresh_token", refreshToken);
        log.info("[API/refresh] payload: {}", Map.of("refresh_token", "***"));

        return publicHttp.post()
                .uri("/auth/v1/token?grant_type=refresh_token")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)   // <-- clave
                .bodyValue(body)
                .exchangeToMono(this::handleTokenResponse)
                .doOnSuccess(out -> log.info("[API/refresh] OK <- keys: {}", out.keySet()))
                .doOnError(err -> log.warn("[API/refresh] ERROR <- {}", err.getMessage()));
    }

    /* ---------------------------------------------------------
     * USER (Bearer access token del usuario)
     * --------------------------------------------------------- */
    public Mono<Map<String, Object>> getUser(String accessToken) {
        return publicHttp.get()
                .uri("/auth/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // token de usuario
                .retrieve()
                .bodyToMono(MAP_OF_OBJECT);
    }

    /* ---------- handlers ---------- */

    @SuppressWarnings("unchecked")
    private Mono<Map<String, Object>> handleRegisterResponse(ClientResponse res, String email) {
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(MAP_OF_OBJECT).flatMap(resp -> {
                Object maybeUser = resp.get("user");
                Map<String, Object> user = (maybeUser instanceof Map)
                        ? (Map<String, Object>) maybeUser
                        : resp;

                if (user.get("id") == null) {
                    return Mono.error(new IllegalStateException(
                            "Respuesta inesperada de Supabase (no viene user/id)."));
                }
                Map<String, Object> out = new HashMap<>();
                out.put("id", user.get("id"));
                out.put("email", user.get("email"));
                out.put("user_metadata", user.get("user_metadata"));
                return Mono.just(out);
            });
        }

        HttpStatusCode sc = res.statusCode();

        return res.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(bodyStr -> {
                    log.warn("[SUPABASE/ADMIN] <-- {} body: {}", sc.value(), bodyStr);

                    Map<String, Object> err = tryParseJsonToMap(bodyStr);

                    String rawMsg = firstNonBlank(
                            str(err.get("message")),
                            str(err.get("error_description")),
                            str(err.get("msg")),
                            str(err.get("hint")),
                            bodyStr
                    );
                    String code = firstNonBlank(
                            str(err.get("error_code")),
                            str(err.get("code")),
                            ""
                    );

                    String msg = rawMsg.isBlank()
                            ? ("Supabase " + sc.value() + " (" + sc + ")")
                            : rawMsg;

                    String low = msg.toLowerCase();

                    if (sc.value() == 422 && (low.contains("already registered")
                            || low.contains("user already registered")
                            || low.contains("duplicate key")
                            || low.contains("users_email_key"))) {
                        return Mono.error(new EmailAlreadyExistsException(email));
                    }
                    if (sc.value() == 422 && (low.contains("password") || code.equalsIgnoreCase("weak_password"))) {
                        return Mono.error(new InvalidPasswordException(msg));
                    }
                    if (sc.value() == 422 && (low.contains("invalid email") || code.equalsIgnoreCase("invalid_email"))) {
                        return Mono.error(new InvalidEmailException(msg));
                    }

                    return Mono.error(new RuntimeException("Supabase (" + sc.value() + "): " + msg));
                });
    }

    private Mono<Map<String, Object>> handleTokenResponse(ClientResponse res) {
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(MAP_OF_OBJECT).flatMap(tokens -> {
                if (tokens.get("access_token") == null) {
                    return Mono.error(new IllegalStateException(
                            "Respuesta inesperada de Supabase (no viene access_token)."));
                }
                return Mono.just(tokens);
            });
        }

        HttpStatusCode sc = res.statusCode();
        return res.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(bodyStr -> {
                    log.warn("[SUPABASE/PUBLIC] <-- {} body: {}", sc.value(), bodyStr);

                    Map<String, Object> err = tryParseJsonToMap(bodyStr);

                    String rawMsg = firstNonBlank(
                            str(err.get("message")),
                            str(err.get("error_description")),
                            str(err.get("msg")),
                            str(err.get("hint")),
                            bodyStr
                    );
                    String code = firstNonBlank(
                            str(err.get("error_code")),
                            str(err.get("code")),
                            ""
                    );
                    String msg = rawMsg.isBlank()
                            ? ("Supabase " + sc.value() + " (" + sc + ")")
                            : rawMsg;
                    String low = msg.toLowerCase();

                    // 400 invalid_grant => credenciales inválidas o refresh token inválido/expirado
                    if (sc.value() == 400 && (code.equalsIgnoreCase("invalid_grant")
                            || low.contains("invalid refresh token")
                            || low.contains("invalid login credentials")
                            || low.contains("invalid email or password"))) {
                        return Mono.error(new InvalidCredentialsException("Credenciales inválidas."));
                    }
                    if (sc.value() == 422 && (low.contains("invalid email") || code.equalsIgnoreCase("invalid_email"))) {
                        return Mono.error(new InvalidEmailException(msg));
                    }

                    return Mono.error(new RuntimeException("Supabase (" + sc.value() + "): " + msg));
                });
    }

    /* ---------- helpers ---------- */

    private static Map<String, Object> redact(Map<String, Object> input) {
        Map<String, Object> copy = new HashMap<>(input);
        if (copy.containsKey("password")) copy.put("password", "***");
        if (copy.containsKey("refresh_token")) copy.put("refresh_token", "***");
        return copy;
    }

    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); }
        catch (Exception e) { return null; }
    }

    private static Double computeBmi(Integer heightCm, Integer weightKg) {
        if (heightCm == null || weightKg == null || heightCm == 0) return null;
        double m = heightCm / 100.0;
        double raw = weightKg / (m * m);
        return Math.round(raw * 10.0) / 10.0;
    }

    private static Integer safeAgeFromDob(String dob) {
        try {
            return Period.between(LocalDate.parse(dob), LocalDate.now()).getYears();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Map<String, Object> tryParseJsonToMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return JSON.readValue(json, new TypeReference<>() {});
        } catch (Exception ignore) {
            return Map.of();
        }
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    private static String firstNonBlank(String... arr) {
        for (String s : arr) if (s != null && !s.isBlank()) return s;
        return "";
    }
}
