package com.board.jobseeker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;
    
    @Value("${CORS_ALLOWED_HEADERS}")
    private String allowedHeaders;
    
    @Value("${CORS_ALLOW_CREDENTIALS}")
    private boolean allowCredentials;
    
    // TODO: IMPLEMENT API OPTIONS HEADER, DOCUMENTATION FOR ORIGINS FORMATTING
    @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Parse comma-separated origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
        
        // allowed methods 
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // allowed headers
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        config.setAllowedHeaders(headers);
        
        // credential sharing
        config.setAllowCredentials(allowCredentials);
        
        // max age for preflight requests
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
        
        // apply config to all /api/** endpoints
        source.registerCorsConfiguration("/api/**", config);
        
        return source; 
    }
}