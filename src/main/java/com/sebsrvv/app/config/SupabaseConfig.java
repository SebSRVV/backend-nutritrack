package com.sebsrvv.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 🔧 Configuración central para la conexión con la API REST de Supabase.
 *
 * ✅ Usa la Service Key como token principal de autenticación.
 * ✅ Crea un cliente WebClient reutilizable para consumir cualquier tabla.
 * ✅ Permite manejar respuestas JSON grandes (hasta 2MB).
 *
 * Ejemplo de uso:
 *   @Autowired
 *   private WebClient supabaseClient;
 *
 *   supabaseClient.get()
 *       .uri("/profiles?select=*")
 *       .retrieve()
 *       .bodyToMono(String.class)
 *       .block();
 *
 * Variables requeridas en application.properties o .env:
 *   supabase.url=https://<your-project>.supabase.co
 *   supabase.serviceKey=<your-service-role-key>
 */
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.serviceKey}")
    private String supabaseServiceKey;

    /**
     * Crea y expone un WebClient configurado para acceder a Supabase REST.
     *
     * @return WebClient configurado con autenticación y cabeceras necesarias.
     */
    @Bean
    public WebClient supabaseClient() {

        // Permite manejar respuestas grandes (hasta 2MB)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();

        return WebClient.builder()
                .baseUrl(supabaseUrl + "/rest/v1") // Endpoint REST de Supabase
                .defaultHeader("apikey", supabaseServiceKey)
                .defaultHeader("Authorization", "Bearer " + supabaseServiceKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
