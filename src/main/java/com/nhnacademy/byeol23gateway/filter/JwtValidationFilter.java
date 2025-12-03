package com.nhnacademy.byeol23gateway.filter;

import io.jsonwebtoken.Claims;
import org.apache.http.HttpHeaders;
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

		log.info("Request URI: {}", path);

		// /api 로 시작하지 않으면 이 필터는 아무 것도 안 하고 통과
		if (!path.startsWith("/api")) {
			return chain.filter(exchange);
		}

		// 1. Authorization 헤더에서 Bearer 토큰 추출
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		log.info("Authorization Header = {}", authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}

		String accessToken = authHeader.substring(7);

		try {
			Claims claims = jwtParser.parse(accessToken);

			Long memberId = claims.get("memberId", Long.class);
			String role = claims.get("role", String.class);

			log.info("JWT 검증 성공 - memberId: {}, role: {}", memberId, role);

			var mutatedRequest = request.mutate()
					.header("X-Member-Id", String.valueOf(memberId))
					.header("X-Role", role != null ? role : "")
					.build();

			return chain.filter(
					exchange.mutate()
							.request(mutatedRequest)
							.build()
			);

		} catch (Exception e) {
			log.warn("JWT 검증 실패: {}", e.getMessage());
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}
	}
}

