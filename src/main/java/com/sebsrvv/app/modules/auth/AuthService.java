package com.sebsrvv.app.modules.auth;

import com.sebsrvv.app.modules.auth.domain.ProfileRepository;
import com.sebsrvv.app.modules.auth.domain.UserProfile;
import com.sebsrvv.app.modules.auth.infra.SupabaseAuthClient;
import com.sebsrvv.app.modules.auth.web.dto.LoginRequest;
import com.sebsrvv.app.modules.auth.web.dto.RegisterRequest;
import com.sebsrvv.app.modules.auth.web.dto.TokenResponse;
import com.sebsrvv.app.modules.auth.web.dto.UpdateProfileRequest;
import com.sebsrvv.app.modules.auth.web.dto.UpdateProfileResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final ProfileRepository profiles;
    private final SupabaseAuthClient supabase;

    public AuthService(ProfileRepository profiles, SupabaseAuthClient supabase) {
        this.profiles = profiles;
        this.supabase = supabase;
    }

    @Transactional
    public TokenResponse register(RegisterRequest r) {
        Map<String, Object> meta = new HashMap<>();
        if (r.username() != null) meta.put("username", r.username());
        if (r.sex() != null) meta.put("sex", r.sex().name().toLowerCase());
        if (r.height_cm() != null) meta.put("height_cm", r.height_cm());
        if (r.weight_kg() != null) meta.put("weight_kg", r.weight_kg());
        if (r.dob() != null) meta.put("dob", r.dob().toString());
        if (r.activity_level() != null) meta.put("activity_level", r.activity_level().dbValue());
        if (r.diet_type() != null) meta.put("diet_type", r.diet_type().dbValue());
        supabase.signup(r.email(), r.password(), meta);
        Map<String, Object> response = supabase.login(r.email(), r.password());
        TokenResponse token = toTokenResponse(response);
        String idStr = asString(token.user().get("id"));
        String email = asString(token.user().get("email"));
        UUID id = idStr == null ? null : UUID.fromString(idStr);
        ensureProfile(id, email, r);
        return token;
    }

    @Transactional
    public TokenResponse login(LoginRequest r) {
        Map<String, Object> response = supabase.login(r.email(), r.password());
        TokenResponse token = toTokenResponse(response);
        String idStr = asString(token.user().get("id"));
        String email = asString(token.user().get("email"));
        UUID id = idStr == null ? null : UUID.fromString(idStr);
        ensureProfile(id, email, null);
        return token;
    }

    @Transactional
    public Map<String, Object> me(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        ensureProfile(userId, email, null);
        return Map.of("id", userId.toString(), "email", email);
    }

    @Transactional
    public UpdateProfileResponse updateProfile(Jwt jwt, UpdateProfileRequest r) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        UserProfile p = ensureProfile(userId, email, null);
        if (r.username() != null && !r.username().isBlank()) p.setUsername(r.username().trim().toLowerCase());
        if (r.sex() != null) p.setSex(r.sex().name().toLowerCase());
        if (r.height_cm() != null) p.setHeightCm(r.height_cm());
        if (r.weight_kg() != null) p.setWeightKg(r.weight_kg());
        if (r.dob() != null) p.setDob(r.dob());
        if (r.activity_level() != null) p.setActivityLevel(r.activity_level().dbValue());
        if (r.diet_type() != null) p.setDietType(r.diet_type().dbValue());
        p.setBmi(computeBmi(p.getHeightCm(), p.getWeightKg()));
        p.setUpdatedAt(Instant.now());
        profiles.save(p);
        return new UpdateProfileResponse(
                userId.toString(),
                p.getUsername(),
                p.getSex(),
                p.getHeightCm(),
                p.getWeightKg(),
                p.getActivityLevel(),
                p.getDietType(),
                p.getBmi(),
                p.getUpdatedAt() == null ? null : p.getUpdatedAt().toString()
        );
    }

    @Transactional
    public void deleteAccount(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        profiles.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        Map<String, Object> resp = supabase.refresh(refreshToken);
        return toTokenResponse(resp);
    }

    @SuppressWarnings("unchecked")
    private TokenResponse toTokenResponse(Map<String, Object> m) {
        Map<String, Object> user = (Map<String, Object>) m.getOrDefault("user", Map.of());
        String at = asString(m.get("access_token"));
        String rt = asString(m.get("refresh_token"));
        String tt = asString(m.get("token_type"));
        Long exp = m.get("expires_in") instanceof Number n ? n.longValue() : null;
        return new TokenResponse(at, rt, tt, exp, user);
    }

    private String asString(Object o) { return o == null ? null : String.valueOf(o); }

    private UserProfile ensureProfile(UUID userId, String email, RegisterRequest r) {
        if (userId == null) return null;
        return profiles.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setId(userId);
            p.setUsername(defaultUsername(userId, email));
            if (r != null) {
                if (r.username() != null) p.setUsername(r.username().trim().toLowerCase());
                if (r.sex() != null) p.setSex(r.sex().name().toLowerCase());
                if (r.height_cm() != null) p.setHeightCm(r.height_cm());
                if (r.weight_kg() != null) p.setWeightKg(r.weight_kg());
                if (r.dob() != null) p.setDob(r.dob());
                if (r.activity_level() != null) p.setActivityLevel(r.activity_level().dbValue());
                if (r.diet_type() != null) p.setDietType(r.diet_type().dbValue());
                p.setBmi(computeBmi(r.height_cm(), r.weight_kg()));
            }
            p.setUpdatedAt(Instant.now());
            return profiles.save(p);
        });
    }

    private String defaultUsername(UUID userId, String email) {
        String base = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9._-]", "")
                : ("user_" + userId.toString().replaceAll("-", "").substring(0, 8));
        String candidate = base.isBlank() ? ("user_" + userId.toString().replaceAll("-", "").substring(0, 8)) : base;
        if (!profiles.existsByUsernameIgnoreCase(candidate)) return candidate;
        int i = 1;
        while (profiles.existsByUsernameIgnoreCase(candidate + i)) i++;
        return candidate + i;
    }

    private static BigDecimal computeBmi(Short heightCm, BigDecimal weightKg) {
        if (heightCm == null || heightCm == 0 || weightKg == null) return null;
        BigDecimal m = BigDecimal.valueOf(heightCm).divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);
        BigDecimal m2 = m.multiply(m);
        BigDecimal raw = weightKg.divide(m2, 3, RoundingMode.HALF_UP);
        return raw.setScale(1, RoundingMode.HALF_UP);
    }
}
