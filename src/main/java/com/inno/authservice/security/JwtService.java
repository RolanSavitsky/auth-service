package com.inno.authservice.security;

import com.inno.authservice.entity.Credentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
                      @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {


        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(Credentials credentials) {
        return buildToken(credentials, "access", accessExpirationMs);
    }

    public String generateRefreshToken(Credentials credentials) {
        return buildToken(credentials, "refresh", refreshExpirationMs);
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(Credentials credentials, String type, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(credentials.getLogin())
                .claim("userId", credentials.getUserId())
                .claim("role", credentials.getRole())
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }
}