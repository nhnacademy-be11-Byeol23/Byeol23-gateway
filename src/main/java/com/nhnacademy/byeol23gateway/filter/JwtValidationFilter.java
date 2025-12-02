package com.nhnacademy.byeol23gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.nhnacademy.byeol23gateway.parser.JwtParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GatewayFilter {

	private final JwtParser jwtParser;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		var request = exchange.getRequest();
		var response = exchange.getResponse();
		String path = request.getPath().toString();
		log.info("URI: {}", path);

		if(path.startsWith("/auth")) {
			String refreshToken = request.getCookies().getFirst("Refresh-Token").getValue();
			log.info("refreshToken = {}", refreshToken);
			if(refreshToken == null || refreshToken.isBlank()) {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
		}
		if(path.startsWith("/api")) {
			String accessToken = request.getCookies().getFirst("Access-Token").getValue();
			log.info("accessToken = {}", accessToken);
			if(accessToken == null || accessToken.isBlank()) {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
		}
		return chain.filter(exchange);
	}
}

