package com.nhnacademy.byeol23gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.byeol23gateway.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/*
		/auth/login은 jwt토큰이 존재하지 않기 때문에 빼주어야 한다.
	 */
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("API-Categories", r -> r.path("/api/categories/**")
				.uri("lb://BYEOL23-BACKEND"))
			.route("API-Categories", r -> r.path("/api/members/register")
				.uri("lb://BYEOL23-BACKEND"))
			.route("API-Request", r -> r.path("/api/**")
				.filters(f -> f.filter(jwtAuthenticationFilter))
				.uri("lb://BYEOL23-BACKEND"))
			.route("AUTH-Login", r -> r.path("/auth/login")
				.uri("lb://BYEOL23-AUTH"))
			.route("AUTH-Request", r -> r.path("/auth/**")
				.filters(f -> f.filter(jwtAuthenticationFilter))
				.uri("lb://BYEOL23-AUTH"))
			.build();
	}
}
