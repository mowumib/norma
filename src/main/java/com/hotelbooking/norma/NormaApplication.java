package com.hotelbooking.norma;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@SpringBootApplication
public class NormaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NormaApplication.class, args);
	}


	@Bean
	OpenAPI myAPI() {
		final Components components = new Components();
			components.addSecuritySchemes("bearer-key",
				new SecurityScheme()
						.type(HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));
			return new OpenAPI()
				.components(components)	
				.addSecurityItem(
					new SecurityRequirement()
						.addList("bearer-jwt", Arrays.asList("read", "write"))
						.addList("bearer-key", Collections.emptyList()))
				.servers(Arrays.asList(
					new Server().url("http://localhost:9530/")
						.description("Local Development URL")));

	}
}
