package com.nhnacademy.byeol23gateway.parser;

import java.security.PublicKey;
import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23gateway.filter.Role;

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

	public boolean isAdmin(String token) {
		Claims claims = parse(token);
		String role = claims.get("role").toString();
		return Objects.equals(role, Role.ADMIN.name());
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
