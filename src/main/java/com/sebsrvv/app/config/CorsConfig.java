// config/CorsConfig.java
package com.sebsrvv.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean public CorsFilter corsFilter() {
        var c = new CorsConfiguration();
        c.addAllowedOriginPattern("*"); c.addAllowedHeader("*"); c.addAllowedMethod("*");
        c.setAllowCredentials(true);
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return new CorsFilter(src);
    }
}
