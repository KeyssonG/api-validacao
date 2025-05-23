package keysson.apis.validacao.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        apiConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        apiConfig.setAllowedHeaders(List.of("*"));
        apiConfig.setAllowCredentials(true);
        apiConfig.setExposedHeaders(List.of("Authorization"));

        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOriginPatterns(List.of("*"));
        swaggerConfig.setAllowedMethods(List.of("GET", "OPTIONS"));
        swaggerConfig.setAllowedHeaders(List.of("Accept", "Content-Type"));

        source.registerCorsConfiguration("/api/**", apiConfig);
        source.registerCorsConfiguration("/login", apiConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);

        return new CorsFilter();
    }
}
