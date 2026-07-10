package com.orinocolabs.cowork_studio.identity.domain.port;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;

/**
 * Outbound port for turning a raw password into a {@code HashedPassword} and
 * verifying one against it. Implemented in infrastructure with Spring
 * Security's {@code PasswordEncoder} (BCrypt) — the domain only knows this
 * contract, never the concrete algorithm.
 */
public interface PasswordHasher {

    HashedPassword hash(String rawPassword);

    boolean matches(String rawPassword, HashedPassword hashedPassword);
}
