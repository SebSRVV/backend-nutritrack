package com.sebsrvv.app.supabase;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 🌐 Configura un WebClient global reutilizable.
 *
 * Soluciona el error:
 * "Parameter 0 of constructor required a bean of type 'WebClient' that could not be found".
 */
@Configuration
public class WebClientConfig {

    /**
     * Crea y expone un WebClient global que puede inyectarse en cualquier clase.
     *
     * Ejemplo:
     *   @Autowired
     *   private WebClient webClient;
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
