package com.nhnacademy.byeol23gateway.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Gateway GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("DecodeingFailureException -> 401 ErrorResponse 매핑")
    void handleDecodingFailureException() {
        // given
        DecodeingFailureException ex = new DecodeingFailureException("디코딩 실패");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/path");

        // when
        ErrorResponse response = handler.handleDecodingFauilureException(ex, request);

        // then
        assertThat(response.status()).isEqualTo(401);
        assertThat(response.message()).isEqualTo("디코딩 실패");
        assertThat(response.path()).isEqualTo("/test/path");
        assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("KeyLoadFailureException -> 401 ErrorResponse 매핑")
    void handleKeyLoadFailureException() {
        // given
        KeyLoadFailureException ex = new KeyLoadFailureException("키 로딩 실패");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/another/path");

        // when
        ErrorResponse response = handler.handleKeyLoadFauilureException(ex, request);

        // then
        assertThat(response.status()).isEqualTo(401);
        assertThat(response.message()).isEqualTo("키 로딩 실패");
        assertThat(response.path()).isEqualTo("/another/path");
        assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}



