package com.sebsrvv.app.modules.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteRequest(
        @NotBlank String confirmation
) {}
