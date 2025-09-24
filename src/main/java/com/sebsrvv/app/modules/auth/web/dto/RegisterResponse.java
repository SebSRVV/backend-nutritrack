// modules/auth/web/dto/RegisterResponse.java
package com.sebsrvv.app.modules.auth.web.dto;

public record RegisterResponse(
        String id,
        String email,
        String username
) {}
