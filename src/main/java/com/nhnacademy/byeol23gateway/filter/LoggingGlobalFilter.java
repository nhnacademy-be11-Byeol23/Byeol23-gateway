package com.nhnacademy.byeol23gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.info("Request IN - ID: [{}], Method: [{}], URI: {}",
                request.getId(), request.getMethod(), request.getURI());

        var requestCookies = request.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (requestCookies != null) {
            requestCookies.forEach(cookie ->
                log.info("Response Cookie => {}", cookie)
            );
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            ServerHttpResponse response = exchange.getResponse();

            log.info("Response OUT - ID: [{}], Status: {}",
                    request.getId(), response.getStatusCode());

            var responseCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (responseCookies != null) {
                responseCookies.forEach(cookie ->
                    log.info("Response Cookie => {}", cookie)
                );
            }
        }));
    }

}