package com.nhnacademy.byeol23gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * byeol23-gateway OpenAPI(Swagger) 설정.
 *
 * - Swagger UI: /swagger-ui.html
 * - OpenAPI JSON: /v3/api-docs
 *
 * 게이트웨이는 실제 비즈니스 컨트롤러 대신 라우팅만 담당하지만,
 * 외부에서 바라보는 공용 진입점의 기본 정보를 문서화하기 위해 정의합니다.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Byeol23 Gateway API",
                version = "v1",
                description = "서비스 간 트래픽을 라우팅하는 Gateway 서버의 공통 엔드포인트 문서"
        ),
        servers = {
                @Server(url = "http://localhost:10336", description = "Local Gateway")
        },
        tags = {
                @Tag(name = "Gateway", description = "게이트웨이 공통 라우팅/에러 응답")
        }
)
public class SwaggerConfig {
}


