package com.nhnacademy.byeol23gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		var request = exchange.getRequest();
		var response = exchange.getResponse();
		String path = request.getPath().toString();
		log.info("URI: {}", path);

		if(path.startsWith("/auth")) {
			String refreshToken = String.valueOf(request.getCookies().getFirst("Refresh-Token"));
			log.info("refreshToken = {}", refreshToken);
			if(refreshToken == null || refreshToken.isBlank()) {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			} else {
				if(isValidToken(refreshToken)) {

				}
			}
		}
		if(path.startsWith("/api")) {
			String accessToken = String.valueOf(request.getCookies().getFirst("Access-Token"));
			log.info("accessToken = {}", accessToken);
			if(accessToken == null || accessToken.isBlank()) {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
		}
		return chain.filter(exchange)
			.then(Mono.fromRunnable(() -> {
				var res = exchange.getResponse();


			}));
	}

	private boolean isValidToken(String token) {

	}

}
