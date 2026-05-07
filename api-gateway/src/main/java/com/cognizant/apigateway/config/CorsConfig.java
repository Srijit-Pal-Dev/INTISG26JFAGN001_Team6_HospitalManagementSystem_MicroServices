package com.cognizant.apigateway.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsWebFilter corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
		config.setAllowCredentials(false);
		config.setAllowedHeaders(List.of("*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		config.setAllowedOriginPatterns(List.of("*"));
		config.setExposedHeaders(List.of("Content-Type", "Authorization", "X-User-Id", "X-User-Role"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}
}
