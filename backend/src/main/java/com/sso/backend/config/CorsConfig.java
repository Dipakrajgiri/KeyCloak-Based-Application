package com.sso.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration
 *
 * WHY WE NEED THIS:
 * - Frontend runs on http://localhost:3000 (Next.js)
 * - Backend runs on http://localhost:8080 (Spring Boot)
 * - Browsers block requests between different origins by default
 * - CORS tells the browser: "Hey, it's okay, let localhost:3000 talk to me"
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Which origins (frontends) can call our API
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));

        // Which HTTP methods are allowed
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Which headers the frontend can send
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Allow the browser to send cookies/auth headers
        configuration.setAllowCredentials(true);

        // How long the browser should cache the CORS preflight response (in seconds)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }
}
