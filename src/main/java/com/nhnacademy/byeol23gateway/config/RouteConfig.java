package com.nhnacademy.byeol23gateway.config;

import com.nhnacademy.byeol23gateway.filter.JwtValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

	private final JwtValidationFilter jwtValidationFilter;

	@Value("${spring.cloud.gateway.server.webflux.routes[0].uri}")
	private String backendPath;

	@Value("${spring.cloud.gateway.server.webflux.routes[1].uri}")
	private String authenticationPath;

	@Value("${spring.cloud.gateway.server.webflux.routes[2].uri}")
	private String searchPath;

	@Value("${spring.cloud.gateway.server.webflux.routes[2].predicates[0]}")
	private String searchURI;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("API-CATEGORIES", r -> r.path("/api/categories/**")
						.uri(backendPath))
				.route("API-MEMBER-REGISTER", r -> r.path("/api/members/register")
						.uri(backendPath))
				.route("API-SECURED", r -> r.path("/api/**")
						.filters(f -> f.filter(jwtValidationFilter))
						.uri(backendPath))
				.route("AUTH", r -> r.path("/auth/**")
						.uri(authenticationPath))
				.route("SEARCH", r -> r.path(searchURI)
						.uri(searchPath))

				.build();
	}
}