/*package com.cognizant.pharmacyService.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<SimpleGrantedAuthority> extractRoles(String token) {
		Claims claims = extractAllClaims(token);
		List<String> roles = claims.get("roles", List.class);

		return roles.stream().map(SimpleGrantedAuthority::new).toList();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build()
				.parseClaimsJws(token).getBody();
	}
}*/