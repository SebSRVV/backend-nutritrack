package com.sebsrvv.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Comma separated list (env CORS_LIST).
     * Ej: "https://app.com,https://admin.app.com,http://localhost:4200"
     */
    private String allowedOrigins = "";

    /**
     * Si la lista queda vacía, ¿permitimos todo? (mejor dejar en false en prod).
     * Puedes sobreescribir en application-dev.properties si quieres.
     */
    private boolean allowAllWhenEmpty = false;

    public String getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }

    public boolean isAllowAllWhenEmpty() { return allowAllWhenEmpty; }
    public void setAllowAllWhenEmpty(boolean allowAllWhenEmpty) { this.allowAllWhenEmpty = allowAllWhenEmpty; }

    /** Lista limpia sin espacios ni vacíos. */
    public List<String> parseOrigins() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) return List.of();
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
