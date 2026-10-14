package com.ecommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        // Derive a key of sufficient length. If secret is short, hash it.
        byte[] bytes = secret.getBytes();
        if (bytes.length < 32) {
            try {
                bytes = java.security.MessageDigest.getInstance("SHA-256").digest(bytes);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        this.key = Keys.hmacShaKeyFor(bytes.length >= 32 ? bytes : pad(bytes));
        this.expirationMs = expirationMs;
    }

    private byte[] pad(byte[] b) {
        byte[] out = new byte[32];
        System.arraycopy(b, 0, out, 0, Math.min(b.length, out.length));
        return out;
    }

    public String generateToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return generateToken(user.getUsername(), determineRole(authentication));
    }

    public String generateToken(String subject, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    private String determineRole(Authentication authentication) {
        return authentication.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        Object role = parseClaims(token).get("role");
        return role == null ? "USER" : role.toString();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
