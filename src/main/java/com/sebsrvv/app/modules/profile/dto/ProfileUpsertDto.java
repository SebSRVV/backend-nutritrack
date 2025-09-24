package com.sebsrvv.app.modules.profile.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ProfileUpsertDto(
        @NotBlank String username,
        @NotNull LocalDate dob,
        @NotBlank String sex,                
        @NotNull @Min(50) @Max(260) Integer height_cm,
        @NotNull @Positive Double weight_kg,
        @NotBlank String activity_level,      
        @NotBlank String diet_type            
) {}
