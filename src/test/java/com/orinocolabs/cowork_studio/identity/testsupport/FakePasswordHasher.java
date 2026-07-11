package com.orinocolabs.cowork_studio.identity.testsupport;

import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;

/**
 * Deterministic stand-in for BCrypt — good enough to assert "not the raw
 * value" and "matches only the same raw value", without pulling Spring
 * Security into a plain unit test.
 */
public final class FakePasswordHasher implements PasswordHasher {

    @Override
    public HashedPassword hash(String rawPassword) {
        return HashedPassword.ofHash(rawPassword.toUpperCase());
    }

    @Override
    public boolean matches(String rawPassword, HashedPassword hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }
}
