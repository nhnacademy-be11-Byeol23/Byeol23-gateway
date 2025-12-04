package com.nhnacademy.byeol23gateway.parser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtParser 테스트")
class JwtParserTest {

    private JwtParser jwtParser;
    private KeyPair keyPair;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();

        jwtParser = new JwtParser(publicKey);
    }

    @Test
    @DisplayName("유효한 JWT 토큰을 파싱할 수 있다")
    void parse_validToken_shouldReturnClaims() {
        // given
        String token = createValidToken();

        // when
        Claims claims = jwtParser.parse(token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("test-user");
    }

    @Test
    @DisplayName("만료되지 않은 토큰은 유효하다")
    void isValid_notExpiredToken_shouldReturnTrue() {
        // given
        String token = createValidToken();

        // when
        boolean isValid = jwtParser.isValid(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않다")
    void isValid_expiredToken_shouldReturnFalse() {
        // given
        String token = createExpiredToken();

        // when
        boolean isValid = jwtParser.isValid(token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 파싱 시 예외가 발생한다")
    void parse_invalidSignature_shouldThrowException() throws Exception {
        // given
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair otherKeyPair = keyPairGenerator.generateKeyPair();

        String token = Jwts.builder()
                .subject("test-user")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(otherKeyPair.getPrivate())
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtParser.parse(token))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 파싱 시 예외가 발생한다")
    void parse_invalidFormat_shouldThrowException() {
        // given
        String invalidToken = "invalid.token.format";

        // when & then
        assertThatThrownBy(() -> jwtParser.parse(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("null 토큰 파싱 시 예외가 발생한다")
    void parse_nullToken_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> jwtParser.parse(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("클레임에서 정보를 추출할 수 있다")
    void parse_shouldExtractClaims() {
        // given
        String token = Jwts.builder()
                .subject("test-user")
                .claim("role", "ADMIN")
                .claim("email", "test@example.com")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(keyPair.getPrivate())
                .compact();

        // when
        Claims claims = jwtParser.parse(token);

        // then
        assertThat(claims.getSubject()).isEqualTo("test-user");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
        assertThat(claims.get("email")).isEqualTo("test@example.com");
    }

    private String createValidToken() {
        return Jwts.builder()
                .subject("test-user")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    private String createExpiredToken() {
        return Jwts.builder()
                .subject("test-user")
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(keyPair.getPrivate())
                .compact();
    }
}

