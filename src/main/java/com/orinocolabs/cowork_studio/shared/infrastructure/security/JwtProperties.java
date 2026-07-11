package com.orinocolabs.cowork_studio.shared.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code app.security.jwt.*} from application.properties. {@code secret}
 * must be at least 256 bits (32 chars) for HS256 — see
 * {@link JwtTokenIssuerAdapter}. Overridden via the {@code JWT_SECRET} /
 * {@code JWT_EXPIRATION_MINUTES} env vars in real environments; never rely
 * on the default outside local development.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, long expirationMinutes) {
}
