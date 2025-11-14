package com.nhnacademy.byeol23gateway.parser;

import java.security.PublicKey;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtParser {

	private final PublicKey publicKey;

	public boolean isValid(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(publicKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
		Date expiration = claims.getExpiration();

		return expiration.after(new Date());
	}
}
