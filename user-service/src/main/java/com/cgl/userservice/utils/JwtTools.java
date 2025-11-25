package com.cgl.userservice.utils;

import com.cgl.userservice.data.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTools {
    @Value("${application.security.jwt.secret}")
    private String secret;

    @Value("${application.security.jwt.expiration-ms}")
    private long expiration;

    private SecretKey signingKey;

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        if (signingKey == null) {
            if (secret == null || secret.trim().isEmpty() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
                signingKey = Jwts.SIG.HS256.key().build();
            } else {
                signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            }
        }
        return signingKey;
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        String role = extractRole(token);
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, User user) {
        return extractEmail(token).equals(user.getEmail()) && extractRole(token).equals(user.getRole().name());
    }
}