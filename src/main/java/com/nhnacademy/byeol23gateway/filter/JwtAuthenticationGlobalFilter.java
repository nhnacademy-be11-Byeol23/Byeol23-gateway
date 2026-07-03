package com.nhnacademy.byeol23gateway.filter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23gateway.exception.ErrorResponse;
import com.nhnacademy.byeol23gateway.parser.JwtParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 게이트웨이 진입 지점에서 JWT의 진위(서명/만료)를 검증하는 글로벌 필터.
 * <p>토큰이 없는 요청은 통과시키고(엔드포인트별 인증 필요 여부는 각 서비스가 판단),
 * 토큰이 있으나 위조/만료된 경우 다운스트림 라우팅 전에 401로 즉시 차단한다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

	private final JwtParser jwtParser;
	private final ObjectMapper objectMapper;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		// 토큰이 없으면 통과. 인증이 필요한 자원이면 다운스트림 서비스가 401을 반환한다.
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		String token = authHeader.substring(7);

		// 위조/만료 토큰은 게이트웨이에서 즉시 차단한다.
		if (!jwtParser.isValid(token)) {
			log.warn("유효하지 않은 JWT 차단 - URI: {}", exchange.getRequest().getURI());
			return unauthorized(exchange);
		}

		return chain.filter(exchange);
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		ErrorResponse body = new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),
			"유효하지 않은 인증 토큰입니다.",
			exchange.getRequest().getPath().value(),
			LocalDateTime.now()
		);

		byte[] bytes;
		try {
			bytes = objectMapper.writeValueAsBytes(body);
		} catch (Exception e) {
			bytes = "{\"status\":401,\"message\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
		}

		DataBuffer buffer = response.bufferFactory().wrap(bytes);
		return response.writeWith(Mono.just(buffer));
	}

	@Override
	public int getOrder() {
		// 라우팅 필터보다 먼저 실행되어 유효하지 않은 토큰을 조기에 차단한다.
		return -1;
	}
}
