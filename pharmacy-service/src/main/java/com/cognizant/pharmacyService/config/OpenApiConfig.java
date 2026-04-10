/*package com.cognizant.pharmacyService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI pharmacyOpenAPI() {

		return new OpenAPI().info(new Info().title("Pharmacy Service API")
				.description("Handles medicine inventory, dispense workflow, billing & notifications").version("1.0.0"))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
						new SecurityScheme().name("Authorization").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
}*/