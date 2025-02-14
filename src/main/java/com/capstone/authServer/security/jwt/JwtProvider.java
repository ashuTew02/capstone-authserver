package com.capstone.authServer.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-in-ms}") long expirationMillis  // Changed to match your property name
    ) {
        this.expirationMillis = expirationMillis;
        // Use the secret directly since it's not Base64 encoded in your properties
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, Long tenantId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .subject(email)
                .claim("tenantId", tenantId)
                // .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // public String getRoleFromToken(String token) {
    //     Claims claims = Jwts.parser()
    //             .verifyWith(key)
    //             .build()
    //             .parseSignedClaims(token)
    //             .getPayload();
    //     String roleObj = claims.get("role", String.class);
    //     return (roleObj != null) ? roleObj : "";
    // }

    public Long getTenantIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tenantId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}