package com.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey; // Symmetric key used to sign and verify JWTs (HS256).
    private final long expirationMs; // Milliseconds until access tokens expire.

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // Derives a valid HS256 key from the configured secret string.
        this.expirationMs = expirationMs; // Stores TTL so issued tokens share one policy.
    }

    public String extractUsername(String token) { // Reads the JWT subject claim (username).
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // Generic helper to pull any claim through a resolver function.
        Claims claims = extractAllClaims(token); // Parses and verifies signature before exposing claims.
        return claimsResolver.apply(claims); // Applies caller-specific projection (subject, expiry, etc.).
    }

    public String generateToken(UserDetails userDetails) { // Builds a signed JWT for an authenticated principal.
        Date now = new Date(); // Issued-at timestamp anchor.
        Date expiry = new Date(now.getTime() + expirationMs); // Expiration bound to configured TTL.
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Standard claim tying token to UserDetailsService lookup.
                .issuedAt(now) // Supports staleness checks and audit trails.
                .expiration(expiry) // Limits window if token leaks.
                .signWith(signingKey) // Binds integrity to server-held secret.
                .compact(); // Serializes to compact JWS string returned to clients.
    }

    public boolean isTokenValid(String token, UserDetails userDetails) { // Ensures JWT belongs to the loaded user and is not expired.
        String subject = extractUsername(token); // Username embedded in token.
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token); // Prevents token reuse across accounts and rejects stale JWTs.
    }

    private boolean isTokenExpired(String token) { // True when exp claim is before now.
        return extractClaim(token, Claims::getExpiration).before(new Date()); // Compares exp to current instant.
    }

    private Claims extractAllClaims(String token) { // Parses signed JWT after cryptographic verification.
        return Jwts.parser()
                .verifyWith(signingKey) // Rejects tampered or wrong-key tokens.
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Typed access to standard and custom claims.
    }
}
