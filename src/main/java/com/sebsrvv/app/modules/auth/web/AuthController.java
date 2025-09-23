// modules/auth/web/AuthController.java
package com.sebsrvv.app.modules.auth.web;

import com.sebsrvv.app.modules.auth.web.dto.RegisterRequest;
import com.sebsrvv.app.modules.auth.application.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String,Object>>> register(@RequestBody @Valid RegisterRequest r) {
        var payload = Map.<String,Object>of(
                "username",   r.username(),
                "email",      r.email(),
                "password",   r.password(),
                "dob",        r.dob(),
                "sex",        r.sex(),
                "height_cm",  r.height_cm(),
                "weight_kg",  r.weight_kg()
        );
        return authService.register(payload)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", e.getMessage()))
                ));
    }
}
