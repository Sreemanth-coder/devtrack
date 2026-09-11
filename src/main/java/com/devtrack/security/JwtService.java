package com.devtrack.security;

import com.devtrack.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Secret must be long enough for HS256 (>= 32 bytes). Validated at startup.
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "devtrack.jwt.secret must be at least 32 bytes long for HS256. " +
                    "Set the JWT_SECRET environment variable to a long random value.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.accessTokenExpirationMs());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public long getAccessTokenExpirationMs() {
        return jwtProperties.accessTokenExpirationMs();
    }

    public Optional<String> extractUsername(String token) {
        return extractClaims(token).map(Claims::getSubject);
    }

    public Optional<Long> extractUserId(String token) {
        return extractClaims(token).map(claims -> claims.get("userId", Number.class))
                .map(Number::longValue);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Optional<Claims> claims = extractClaims(token);
        if (claims.isEmpty()) {
            return false;
        }
        String username = claims.get().getSubject();
        Date expiration = claims.get().getExpiration();
        return username.equals(userDetails.getUsername()) && expiration.after(new Date());
    }

    private Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
