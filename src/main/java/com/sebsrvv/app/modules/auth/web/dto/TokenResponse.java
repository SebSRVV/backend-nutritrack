// ruta: src/main/java/com/sebsrvv/app/modules/auth/web/dto/TokenResponse.java
package com.sebsrvv.app.modules.auth.web.dto;

import java.util.Map;

public record TokenResponse(
        String access_token,
        String refresh_token,
        String token_type,
        Long expires_in,
        Map<String, Object> user
) {}
