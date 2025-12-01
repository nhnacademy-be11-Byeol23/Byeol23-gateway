package com.nhnacademy.byeol23gateway.parser;

import java.security.PublicKey;
import java.util.Date;

import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtParser {

	private final PublicKey publicKey;

	public boolean isValid(String token) {
		Claims claims = parse(token);
		Date expiration = claims.getExpiration();

		return expiration.after(new Date());
	}

	private Claims parse(String token) {
		return Jwts.parser()
			.verifyWith(publicKey)
			.clockSkewSeconds(60)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
