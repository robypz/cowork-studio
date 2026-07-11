package com.orinocolabs.cowork_studio.identity.infrastructure.out.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.shared.infrastructure.security.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Unlike the handler tests, this one deliberately exercises the real jjwt
 * library (no fakes) — it's the one point in the identity context where we
 * actually need to prove the token round-trips: signed with the configured
 * secret, subject is the user id, role is carried as a claim.
 */
class JwtTokenIssuerAdapterTest {

    private static final String SECRET = "test-only-secret-key-please-32-bytes-min";

    private final JwtProperties properties = new JwtProperties(SECRET, 60);
    private final JwtTokenIssuerAdapter adapter = new JwtTokenIssuerAdapter(properties);

    @Test
    void issues_a_token_whose_claims_round_trip() {
        UserId userId = UserId.newId();

        String token = adapter.issueAccessToken(userId, Role.ADMIN);
        assertTrue(token.chars().filter(c -> c == '.').count() == 2, "should look like a JWT (header.payload.signature)");

        SecretKey signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(userId.value().toString(), claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
