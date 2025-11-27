package com.nhnacademy.byeol23gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.byeol23gateway.filter.JwtValidationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

	private final JwtValidationFilter jwtAuthenticationFilter;
	private final RoleCheckFilter roleCheckFilter;

	@Value("${spring.cloud.gateway.server.webflux.routes[0].uri}")
	private String backendPath;

	@Value("${spring.cloud.gateway.server.webflux.routes[1].uri}")
	private String authenticationPath;
	/*
		/auth/login은 jwt토큰이 존재하지 않기 때문에 빼주어야 한다.
		/api/admin 도 추가해야 함
	 */
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("API-Categories", r -> r.path("/api/members/register")
				.uri(backendPath))
			.route("API-Request", r -> r.path("/api/members")
				.filters(f -> f.filter(jwtAuthenticationFilter))
				.uri(backendPath))
			.route("AUTH-Login", r -> r.path("/auth/login")
				.uri(authenticationPath))
			.route("AUTH-Request", r -> r.path("/auth/**")
				.filters(f -> f.filter(jwtAuthenticationFilter))
				.uri(authenticationPath))
			.build();
	}
}
