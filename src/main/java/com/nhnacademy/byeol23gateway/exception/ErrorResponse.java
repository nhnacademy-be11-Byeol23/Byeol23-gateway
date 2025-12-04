package com.nhnacademy.byeol23gateway.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답 스키마")
public record ErrorResponse(
	@Schema(description = "HTTP 상태 코드", example = "401")
	int status,
	@Schema(description = "에러 메시지", example = "JWT 디코딩 실패")
	String message,
	@Schema(description = "요청 경로", example = "/api/categories")
	String path,
	@Schema(description = "에러 발생 시각", example = "2024-01-01T12:00:00")
	LocalDateTime timestamp
) {
}
