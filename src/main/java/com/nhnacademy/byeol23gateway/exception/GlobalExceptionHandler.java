package com.nhnacademy.byeol23gateway.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Tag(name = "Gateway", description = "게이트웨이 공통 라우팅/에러 응답")
public class GlobalExceptionHandler {

	@Operation(
		summary = "JWT 디코딩 실패 에러 처리",
		description = "JWT 토큰 디코딩에 실패한 경우 401 Unauthorized 응답을 반환합니다."
	)
	@ApiResponse(
		responseCode = "401",
		description = "인증 실패",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	)
	@ExceptionHandler(DecodeingFailureException.class)
	public ErrorResponse handleDecodingFauilureException(DecodeingFailureException e, HttpServletRequest request) {
		return new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),  //401
			e.getMessage(),
			request.getRequestURI(),
			LocalDateTime.now()
		);
	}

	@Operation(
		summary = "키 로딩 실패 에러 처리",
		description = "JWT 공개키 로딩에 실패한 경우 401 Unauthorized 응답을 반환합니다."
	)
	@ApiResponse(
		responseCode = "401",
		description = "인증 실패",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	)
	@ExceptionHandler(KeyLoadFailureException.class)
	public ErrorResponse handleKeyLoadFauilureException(KeyLoadFailureException e, HttpServletRequest request) {
		return new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),  //401
			e.getMessage(),
			request.getRequestURI(),
			LocalDateTime.now()
		);
	}
}
