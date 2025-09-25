package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.auth.application.AuthService;
import com.sebsrvv.app.modules.auth.web.dto.UpdateProfileRequest;
import com.sebsrvv.app.modules.auth.web.dto.UpdateProfileResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /** Editar perfil del usuario autenticado.
     *  Rutas soportadas:
     *    - PUT /api/users/edit   (nueva)
     *    - PUT /api/users/me     (compatibilidad)
     */
    @PutMapping({"/edit", "/me"})
    public Mono<ResponseEntity<UpdateProfileResponse>> updateMe(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateProfileRequest body
    ) {
        if (authHeader == null || authHeader.isBlank()) {
            return Mono.just(ResponseEntity.status(401).<UpdateProfileResponse>build());
        }
        String token = authHeader.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if (token.isBlank()) {
            return Mono.just(ResponseEntity.status(401).<UpdateProfileResponse>build());
        }

        return authService.updateProfile(token, body)
                .map(ResponseEntity::ok)
                // Importante: no enmascarar todo como 400; dejamos que ApiExceptionHandler responda.
                .doOnError(ex -> log.error("[PUT /api/users/edit] error", ex));
    }
}
