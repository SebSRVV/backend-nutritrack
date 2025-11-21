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

    // ---- Helpers de cálculo de perfil ----
    private Integer computeAge(java.time.LocalDate dob) {
        if (dob == null) return null;
        java.time.LocalDate today = java.time.LocalDate.now();
        int age = today.getYear() - dob.getYear();
        if (today.getDayOfYear() < dob.getDayOfYear()) age--;
        return Math.max(age, 0);
    }

    private Integer computeDaysToBirthday(java.time.LocalDate dob) {
        if (dob == null) return null;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate next = dob.withYear(today.getYear());
        if (!next.isAfter(today)) next = next.plusYears(1);
        return (int) java.time.temporal.ChronoUnit.DAYS.between(today, next);
    }

    /**
     * Calcula recomendación de kcal con Mifflin–St Jeor + factor de actividad y ajuste de dieta.
     * sex: "male" o "female". height_cm en cm, weight_kg en kg.
     */
    private Integer computeRecommendedKcal(UserProfile p) {
        if (p == null) return null;
        Short h = p.getHeightCm();
        BigDecimal w = p.getWeightKg();
        java.time.LocalDate dob = p.getDob();
        if (h == null || h == 0 || w == null || dob == null) return null;
        Integer age = computeAge(dob);
        if (age == null) return null;

        String sex = Optional.ofNullable(p.getSex()).orElse("").toLowerCase();
        double height = h.doubleValue();
        double weight = w.doubleValue();
        double bmr = sex.equals("male")
                ? 10*weight + 6.25*height - 5*age + 5
                : 10*weight + 6.25*height - 5*age - 161;

        String activity = Optional.ofNullable(p.getActivityLevel()).orElse("moderate");
        double factor = switch (activity) {
            case "sedentary" -> 1.2;
            case "very_active" -> 1.725;
            default -> 1.55; // moderate
        };

        String diet = Optional.ofNullable(p.getDietType()).orElse("caloric_deficit");
        double adj = switch (diet) {
            case "surplus" -> 1.10;
            case "caloric_deficit" -> 0.85;
            default -> 1.00; // maintenance
        };

        long rec = Math.round(bmr * factor * adj);
        return rec <= 0 ? null : (int) rec;
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
        return getProfile(jwt);
    }

    @Transactional
    public Map<String, Object> getProfile(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        UserProfile p = ensureProfile(userId, email, null);

        // Hidratar datos faltantes desde los metadatos del JWT (Supabase user_metadata)
        try {
            Object raw = jwt.getClaims().get("user_metadata");
            if (raw instanceof java.util.Map<?,?> meta) {
                Object v;
                if (p.getUsername() == null && (v = meta.get("username")) != null) {
                    String u = String.valueOf(v).trim().toLowerCase(); if (!u.isBlank()) p.setUsername(u);
                }
                if (p.getSex() == null && (v = meta.get("sex")) != null) {
                    p.setSex(String.valueOf(v).trim().toLowerCase());
                }
                if (p.getHeightCm() == null && ((v = meta.get("height_cm")) != null || (v = meta.get("heightCm")) != null)) {
                    Short h = null;
                    if (v instanceof Number n) h = n.shortValue();
                    else { try { h = Short.valueOf(String.valueOf(v)); } catch (Exception ignored) {} }
                    if (h != null && h > 0) p.setHeightCm(h);
                }
                if (p.getWeightKg() == null && ((v = meta.get("weight_kg")) != null || (v = meta.get("weightKg")) != null)) {
                    java.math.BigDecimal w = null;
                    if (v instanceof Number n) w = new java.math.BigDecimal(n.toString());
                    else { try { w = new java.math.BigDecimal(String.valueOf(v)); } catch (Exception ignored) {} }
                    if (w != null && w.signum() > 0) p.setWeightKg(w);
                }
                if (p.getDob() == null && (v = meta.get("dob")) != null) {
                    try { p.setDob(java.time.LocalDate.parse(String.valueOf(v))); } catch (Exception ignored) {}
                }
                if (p.getActivityLevel() == null && ((v = meta.get("activity_level")) != null || (v = meta.get("activityLevel")) != null)) {
                    p.setActivityLevel(String.valueOf(v).toLowerCase());
                }
                if (p.getDietType() == null && ((v = meta.get("diet_type")) != null || (v = meta.get("dietType")) != null)) {
                    p.setDietType(String.valueOf(v).toLowerCase());
                }
                // Recalcular BMI si procede
                p.setBmi(computeBmi(p.getHeightCm(), p.getWeightKg()));
                p.setUpdatedAt(Instant.now());
                profiles.save(p);
            }
        } catch (Exception ignored) {}

        Integer age = computeAge(p.getDob());
        Integer daysToBirthday = computeDaysToBirthday(p.getDob());
        Integer recommended = computeRecommendedKcal(p);

        Map<String, Object> m = new HashMap<>();
        m.put("id", userId.toString());
        m.put("email", email);
        m.put("username", p.getUsername());
        m.put("sex", p.getSex());
        m.put("height_cm", p.getHeightCm());
        m.put("weight_kg", p.getWeightKg());
        m.put("dob", p.getDob());
        m.put("activity_level", Optional.ofNullable(p.getActivityLevel()).orElse("moderate"));
        m.put("diet_type", Optional.ofNullable(p.getDietType()).orElse("caloric_deficit"));
        m.put("bmi", p.getBmi());
        m.put("age", age);
        m.put("days_to_birthday", daysToBirthday);
        m.put("recommended_kcal", recommended);
        return m;
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

        Integer age = computeAge(p.getDob());
        Integer daysToBirthday = computeDaysToBirthday(p.getDob());
        Integer recommended = computeRecommendedKcal(p);

        return new UpdateProfileResponse(
                userId.toString(),
                p.getUsername(),
                p.getSex(),
                p.getHeightCm(),
                p.getWeightKg(),
                p.getActivityLevel(),
                p.getDietType(),
                p.getBmi(),
                age,
                daysToBirthday,
                recommended,
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
