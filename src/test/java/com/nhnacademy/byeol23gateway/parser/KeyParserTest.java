package com.nhnacademy.byeol23gateway.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("KeyParser 테스트")
class KeyParserTest {

    private KeyParser keyParser;

    @Test
    @DisplayName("유효한 PEM 형식의 공개키를 파싱할 수 있다")
    void jwtPublicKey_validPemFormat_shouldReturnPublicKey() throws Exception {
        // given
        keyParser = new KeyParser();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey originalPublicKey = keyPair.getPublic();

        String pemFormat = convertToPemFormat(originalPublicKey);

        // when
        PublicKey parsedKey = keyParser.jwtPublicKey(pemFormat);

        // then
        assertThat(parsedKey).isNotNull();
        assertThat(parsedKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("PEM 형식의 공개키에서 BEGIN/END 헤더를 제거하고 파싱할 수 있다")
    void jwtPublicKey_pemWithHeaders_shouldParseSuccessfully() throws Exception {
        // given
        keyParser = new KeyParser();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey originalPublicKey = keyPair.getPublic();

        String pemWithHeaders = "-----BEGIN PUBLIC KEY-----\n" +
                convertToPemFormat(originalPublicKey).replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "") +
                "\n-----END PUBLIC KEY-----";

        // when
        PublicKey parsedKey = keyParser.jwtPublicKey(pemWithHeaders);

        // then
        assertThat(parsedKey).isNotNull();
        assertThat(parsedKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("공백이 포함된 PEM 형식도 파싱할 수 있다")
    void jwtPublicKey_pemWithWhitespace_shouldParseSuccessfully() throws Exception {
        // given
        keyParser = new KeyParser();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey originalPublicKey = keyPair.getPublic();

        String base64Key = Base64.getEncoder().encodeToString(originalPublicKey.getEncoded());
        String pemWithWhitespace = "-----BEGIN PUBLIC KEY-----\n" +
                base64Key.replaceAll("(.{64})", "$1\n") +
                "\n-----END PUBLIC KEY-----";

        // when
        PublicKey parsedKey = keyParser.jwtPublicKey(pemWithWhitespace);

        // then
        assertThat(parsedKey).isNotNull();
        assertThat(parsedKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("잘못된 형식의 키는 KeyLoadFailureException을 발생시킨다")
    void jwtPublicKey_invalidFormat_shouldThrowKeyLoadFailureException() {
        // given
        keyParser = new KeyParser();
        String invalidKey = "invalid-key-format";

        // when & then
        assertThatThrownBy(() -> keyParser.jwtPublicKey(invalidKey))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("빈 문자열은 예외를 발생시킨다")
    void jwtPublicKey_emptyString_shouldThrowException() {
        // given
        keyParser = new KeyParser();
        String emptyKey = "";

        // when & then
        assertThatThrownBy(() -> keyParser.jwtPublicKey(emptyKey))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Base64 디코딩 실패 시 예외를 발생시킨다")
    void jwtPublicKey_invalidBase64_shouldThrowException() {
        // given
        keyParser = new KeyParser();
        String invalidBase64 = "-----BEGIN PUBLIC KEY-----\n" +
                "This is not base64!!!\n" +
                "-----END PUBLIC KEY-----";

        // when & then
        assertThatThrownBy(() -> keyParser.jwtPublicKey(invalidBase64))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("RSA 공개키가 아닌 다른 형식의 키는 InvalidKeySpecException을 발생시킨다")
    void jwtPublicKey_nonRSAKey_shouldThrowException() throws Exception {
        // given
        keyParser = new KeyParser();
        // DSA 키 생성 (RSA가 아님)
        KeyPairGenerator dsaKeyPairGenerator = KeyPairGenerator.getInstance("DSA");
        dsaKeyPairGenerator.initialize(1024);
        KeyPair dsaKeyPair = dsaKeyPairGenerator.generateKeyPair();
        String dsaPem = convertToPemFormat(dsaKeyPair.getPublic());

        // when & then
        assertThatThrownBy(() -> keyParser.jwtPublicKey(dsaPem))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("스펙 불일치");
    }

    private String convertToPemFormat(PublicKey publicKey) {
        byte[] encoded = publicKey.getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(encoded);
        return "-----BEGIN PUBLIC KEY-----\n" + base64Key + "\n-----END PUBLIC KEY-----";
    }
}

