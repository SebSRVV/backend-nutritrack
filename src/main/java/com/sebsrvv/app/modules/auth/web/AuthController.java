package com.sebsrvv.app.modules.auth.web;

import com.sebsrvv.app.modules.auth.AuthService;
import com.sebsrvv.app.modules.auth.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public TokenResponse register(@RequestBody RegisterRequest r) {
        return service.register(r);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest r) {
        return service.login(r);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return service.me(jwt);
    }

    @PatchMapping("/profile")
    public UpdateProfileResponse update(@AuthenticationPrincipal Jwt jwt,
                                        @RequestBody UpdateProfileRequest r) {
        return service.updateProfile(jwt, r);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt) {
        service.deleteAccount(jwt);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshRequest r) {
        return service.refresh(r.refresh_token());
    }
}
