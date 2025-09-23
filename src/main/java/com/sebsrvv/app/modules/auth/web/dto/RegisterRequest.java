// modules/auth/web/dto/RegisterRequest.java
package com.sebsrvv.app.modules.auth.web.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 24) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String dob,          // ISO: "YYYY-MM-DD"
        @NotBlank String sex,          // "male" | "female"
        @Min(80)  @Max(230) Integer height_cm,
        @Min(25)  @Max(250) Integer weight_kg
) {}
