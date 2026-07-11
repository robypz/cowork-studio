package com.orinocolabs.cowork_studio.identity.infrastructure.out.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.orinocolabs.cowork_studio.identity.domain.port.TokenIssuer;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.shared.infrastructure.security.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Implements the domain's {@link TokenIssuer} port with a signed JWT
 * (HS256). The subject is the user id and the {@code role} claim carries
 * {@link Role} — that's the only shape the rest of the system should ever
 * need to know about tokens.
 */
@Component
public class JwtTokenIssuerAdapter implements TokenIssuer {

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtTokenIssuerAdapter(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(properties.expirationMinutes());
    }

    @Override
    public String issueAccessToken(UserId userId, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.value().toString())
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }
}
