// modules/users/domain/UserProfile.java
package com.sebsrvv.app.modules.users.domain;

public record UserProfile(
        String id, String email, String username,
        String dob, String sex, Integer height_cm, Integer weight_kg, Double bmi
) {}
