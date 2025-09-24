package com.sebsrvv.app.modules.auth.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sebsrvv.app.modules.auth.domain.Sex;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(

        @NotBlank @Size(min = 3, max = 24)
        String username,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 6)
        String password,

        @NotNull
        @Past(message = "La fecha de nacimiento debe estar en el pasado")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // JSON -> LocalDate
        LocalDate dob,

        @NotNull
        Sex sex,                            // MALE | FEMALE

        @NotNull @Min(80)  @Max(230)
        Integer height_cm,

        @NotNull @Min(25)  @Max(250)
        Integer weight_kg
) {}
