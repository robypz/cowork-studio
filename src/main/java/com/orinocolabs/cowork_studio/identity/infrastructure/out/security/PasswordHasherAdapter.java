package com.orinocolabs.cowork_studio.identity.infrastructure.out.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;

/**
 * Implements the domain's {@link PasswordHasher} port with Spring Security's
 * {@link PasswordEncoder} (BCrypt, see {@code SecurityConfig}). The domain
 * never learns which algorithm is behind this — only that it exists.
 */
@Component
public class PasswordHasherAdapter implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasherAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HashedPassword hash(String rawPassword) {
        return HashedPassword.ofHash(passwordEncoder.encode(rawPassword));
    }

    @Override
    public boolean matches(String rawPassword, HashedPassword hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword.value());
    }
}
