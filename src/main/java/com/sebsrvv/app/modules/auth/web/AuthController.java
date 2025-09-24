package com.sebsrvv.app.modules.auth.web;

import com.sebsrvv.app.modules.auth.application.AuthService;
import com.sebsrvv.app.modules.auth.web.dto.RegisterRequest;
import com.sebsrvv.app.modules.auth.web.dto.RegisterResponse;
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
}
