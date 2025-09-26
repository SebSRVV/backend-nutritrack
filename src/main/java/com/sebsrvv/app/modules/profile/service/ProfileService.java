package com.sebsrvv.app.modules.profile.service;

import com.sebsrvv.app.modules.profile.dto.*;
import com.sebsrvv.app.modules.profile.entity.Profile;
import com.sebsrvv.app.modules.profile.entity.UserRecommendation;
import com.sebsrvv.app.modules.profile.repo.ProfileRepository;
import com.sebsrvv.app.modules.profile.repo.UserRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepo;
    private final UserRecommendationRepository recRepo;

    public Optional<ProfileView> get(UUID userId) {
        return profileRepo.findById(userId).map(this::toView);
    }

    public Optional<RecommendationView> getRecommendations(UUID userId) {
        return recRepo.findById(userId).map(r -> new RecommendationView(
                r.getAge_years(),
                round2(r.getBmr_kcal()), round2(r.getTdee_kcal()), r.getGoal_kcal(),
                r.getWater_ml(), r.getActivity_factor(), r.getDiet_adjustment(),
                r.getWater_factor_ml_per_kg(), r.getWater_activity_bonus_ml(),
                r.getMethod()
        ));
    }

    @Transactional
    public ProfileView upsert(UUID userId, ProfileUpsertDto dto) {
        // 1) upsert profile
        Profile p = profileRepo.findById(userId).orElseGet(() -> {
            Profile np = new Profile();
            np.setId(userId);
            return np;
        });
        p.setUsername(dto.username());
        p.setDob(dto.dob());
        p.setSex(dto.sex());
        p.setHeight_cm(dto.height_cm());
        p.setWeight_kg(dto.weight_kg());
        p.setActivity_level(dto.activity_level());
        p.setDiet_type(dto.diet_type());
        p.setBmi(calcBMI(dto.weight_kg(), dto.height_cm()));
        profileRepo.save(p);

        // 2) (re)calcular recomendaciones
        UserRecommendation rec = recRepo.findById(userId).orElseGet(() -> {
            UserRecommendation r = new UserRecommendation();
            r.setUser_id(userId);
            return r;
        });

        int age = calcAge(dto.dob());
        double actFactor = mapActivityFactor(dto.activity_level());
        int waterFactor = 35; // ml por kg
        int waterBonus = mapWaterBonus(dto.activity_level());
        double dietAdj = mapDietAdjustment(dto.diet_type()); // -0.10, 0, +0.10

        double bmr = mifflinStJeor(dto.sex(), dto.weight_kg(), dto.height_cm(), age);
        double tdee = bmr * actFactor;
        int goalKcal = (int)Math.round(tdee * (1.0 + dietAdj));
        int waterMl = (int)Math.round(dto.weight_kg() * waterFactor + waterBonus);

        rec.setSex(dto.sex());
        rec.setDob(dto.dob());
        rec.setAge_years(age);
        rec.setHeight_cm(dto.height_cm());
        rec.setWeight_kg(dto.weight_kg());
        rec.setActivity_level(dto.activity_level());
        rec.setActivity_factor(actFactor);
        rec.setDiet_type(dto.diet_type());
        rec.setDiet_adjustment(dietAdj);
        rec.setWater_factor_ml_per_kg(waterFactor);
        rec.setWater_activity_bonus_ml(waterBonus);
        rec.setBmr_kcal(bmr);
        rec.setTdee_kcal(tdee);
        rec.setGoal_kcal(goalKcal);
        rec.setWater_ml(waterMl);
        rec.setMethod("mifflin_2025_v1");
        recRepo.save(rec);

        return toView(p);
    }

    // -------- helpers --------
    private ProfileView toView(Profile p) {
        return new ProfileView(
                p.getId(), p.getUsername(), p.getDob(), p.getSex(),
                p.getHeight_cm(), p.getWeight_kg(), p.getBmi(),
                p.getActivity_level(), p.getDiet_type(),
                p.getCreated_at(), p.getUpdated_at()
        );
    }

    private static int calcAge(LocalDate dob) {
        return (int) ChronoUnit.YEARS.between(dob, LocalDate.now());
    }

    private static double calcBMI(double weightKg, int heightCm) {
        double h = heightCm / 100.0;
        return round2(weightKg / (h * h));
    }

    private static double mapActivityFactor(String lvl) {
        return switch (lvl == null ? "" : lvl.toLowerCase()) {
            case "light"     -> 1.375;
            case "moderate"  -> 1.55;
            case "active"    -> 1.725;
            default /*sedentary*/ -> 1.20;
        };
    }

    private static int mapWaterBonus(String lvl) {
        return switch (lvl == null ? "" : lvl.toLowerCase()) {
            case "moderate"  -> 250;
            case "active"    -> 500;
            default          -> 0;
        };
    }

    private static double mapDietAdjustment(String diet) {
        return switch (diet == null ? "" : diet.toLowerCase()) {
            case "cut_10"  -> -0.10;
            case "bulk_10" -> +0.10;
            default /*maintain*/ -> 0.0;
        };
    }

    // Mifflin–St Jeor
    private static double mifflinStJeor(String sex, double wKg, int hCm, int ageYears) {
        double base = 10*wKg + 6.25*hCm - 5*ageYears;
        return ("female".equalsIgnoreCase(sex)) ? base - 161 : base + 5;
    }

    private static double round2(Double v) {
        if (v == null) return 0.0;
        return Math.round(v * 100.0) / 100.0;
    }
}
