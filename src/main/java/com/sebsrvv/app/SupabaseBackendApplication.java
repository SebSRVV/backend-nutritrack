// src/main/java/com/sebsrvv/app/SupabaseBackendApplication.java
package com.sebsrvv.app;

import com.sebsrvv.app.supabase.SupabaseStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SupabaseStorageProperties.class)
public class SupabaseBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupabaseBackendApplication.class, args);
    }
}