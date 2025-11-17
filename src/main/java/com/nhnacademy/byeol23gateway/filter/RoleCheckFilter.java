package com.nhnacademy.byeol23gateway.filter;

import java.util.Objects;

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
public class RoleCheckFilter implements GatewayFilter {

	private final JwtParser jwtParser;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("Start Role Check Filtering");
		var request = exchange.getRequest();
		var response = exchange.getResponse();

		String token = Objects.requireNonNull(request.getCookies().getFirst("Access-Token")).getValue();

		if(!jwtParser.isValid(token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}

		if(!jwtParser.isAdmin(token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}

		return chain.filter(exchange);
	}
}
