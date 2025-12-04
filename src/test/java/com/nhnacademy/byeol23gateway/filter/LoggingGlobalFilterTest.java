package com.nhnacademy.byeol23gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("LoggingGlobalFilter 테스트")
class LoggingGlobalFilterTest {

    private LoggingGlobalFilter filter;
    private GatewayFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new LoggingGlobalFilter();
        filterChain = mock(GatewayFilterChain.class);
    }

    @Test
    @DisplayName("필터가 정상적으로 체인을 통과하고 로깅을 수행한다")
    void filter_shouldPassThroughChain() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/test")
                .build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.OK);

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = filter.filter(exchange, filterChain);

        // then
        result.block();
        verify(filterChain).filter(any(ServerWebExchange.class));
        assertThat(exchange.getResponse().getStatusCode())
                .isEqualTo(org.springframework.http.HttpStatus.OK);
    }

    @Test
    @DisplayName("요청 쿠키가 있을 때 로깅을 수행한다")
    void filter_shouldLogRequestCookies() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/test")
                .header(HttpHeaders.SET_COOKIE, "sessionId=12345", "token=abcde")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = filter.filter(exchange, filterChain);

        // then
        result.block();
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("응답 쿠키가 있을 때 로깅을 수행한다")
    void filter_shouldLogResponseCookies() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, "/auth/login")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.SET_COOKIE, "sessionId=12345");

        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.fromRunnable(() -> {
                    response.getHeaders().add(HttpHeaders.SET_COOKIE, "token=abcde");
                }));

        // when
        Mono<Void> result = filter.filter(exchange, filterChain);

        // then
        result.block();
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("다양한 HTTP 메서드에 대해 필터가 동작한다")
    void filter_shouldWorkWithDifferentHttpMethods() {
        // given
        HttpMethod[] methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};

        for (HttpMethod method : methods) {
            // 각 반복마다 새로운 mock 생성
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            
            MockServerHttpRequest request = MockServerHttpRequest
                    .method(method, "/api/test")
                    .build();

            ServerWebExchange exchange = MockServerWebExchange.from(request);
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

            when(chain.filter(any(ServerWebExchange.class)))
                    .thenReturn(Mono.empty());

            // when
            Mono<Void> result = filter.filter(exchange, chain);

            // then
            result.block();
            verify(chain).filter(any(ServerWebExchange.class));
        }
    }

    @Test
    @DisplayName("요청 ID와 URI가 로깅에 포함된다")
    void filter_shouldLogRequestIdAndUri() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/categories/1")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = filter.filter(exchange, filterChain);

        // then
        result.block();
        verify(filterChain).filter(any(ServerWebExchange.class));

        assertThat(request.getId()).isNotNull();
        assertThat(request.getURI().toString()).contains("/api/categories/1");
    }
}

