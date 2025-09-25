package com.sebsrvv.app.modules.auth.web;

import com.sebsrvv.app.modules.auth.application.AuthService;
import com.sebsrvv.app.modules.auth.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    /* ---------------------------------------------------------
     * REGISTER
     * --------------------------------------------------------- */
    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponse>> register(@Valid @RequestBody RegisterRequest r) {
        var payload = Map.<String,Object>of(
                "username",   r.username(),
                "email",      r.email(),
                "password",   r.password(),
                "dob",        r.dob().toString(),              // LocalDate -> "YYYY-MM-DD"
                "sex",        r.sex().name().toLowerCase(),    // enum -> "male"/"female"
                "height_cm",  r.height_cm(),
                "weight_kg",  r.weight_kg()
        );

        return authService.register(payload)
                .map(body -> {
                    String id = String.valueOf(body.get("id"));
                    RegisterResponse resp = new RegisterResponse(id, r.email(), r.username());
                    return ResponseEntity
                            .created(URI.create("/api/users/" + id))  // 201 Created
                            .body(resp);
                });
    }

    /* ---------------------------------------------------------
     * LOGIN
     * --------------------------------------------------------- */
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest r) {
        return authService.login(r.email(), r.password())
                .map(tokens -> {
                    LoginResponse resp = new LoginResponse(
                            String.valueOf(tokens.get("access_token")),
                            String.valueOf(tokens.get("refresh_token")),
                            String.valueOf(tokens.getOrDefault("token_type", "bearer")),
                            (Integer) tokens.getOrDefault("expires_in", null),
                            (Map<String, Object>) tokens.getOrDefault("user", null)
                    );
                    return ResponseEntity.ok(resp);
                });
    }

    /* ---------------------------------------------------------
     * REFRESH
     * --------------------------------------------------------- */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest r) {
        return authService.refresh(r.refreshToken())
                .map(tokens -> ResponseEntity.ok(
                        new RefreshResponse(
                                String.valueOf(tokens.get("access_token")),
                                String.valueOf(tokens.get("refresh_token")),
                                String.valueOf(tokens.getOrDefault("token_type", "bearer")),
                                (Integer) tokens.getOrDefault("expires_in", null),
                                (Map<String, Object>) tokens.getOrDefault("user", null)
                        )
                ));
    }

    /* ---------------------------------------------------------
     * ME
     * --------------------------------------------------------- */
    @GetMapping("/me")
    public Mono<ResponseEntity<MeResponse>> me(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        String token = authHeader.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if (token.isBlank()) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        return authService.getUser(token)
                .map(user -> {
                    String id   = String.valueOf(user.get("id"));
                    String email = String.valueOf(user.get("email"));
                    String role  = String.valueOf(user.getOrDefault("role", "authenticated"));
                    @SuppressWarnings("unchecked")
                    Map<String,Object> appMeta = (Map<String, Object>) user.getOrDefault("app_metadata", Map.of());
                    @SuppressWarnings("unchecked")
                    Map<String,Object> userMeta = (Map<String, Object>) user.getOrDefault("user_metadata", Map.of());
                    String createdAt = String.valueOf(user.getOrDefault("created_at", null));
                    String updatedAt = String.valueOf(user.getOrDefault("updated_at", null));

                    MeResponse resp = new MeResponse(id, email, role, appMeta, userMeta, createdAt, updatedAt);
                    return ResponseEntity.ok(resp);
                })
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(401).build()));
    }
}
