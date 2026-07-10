package com.orinocolabs.cowork_studio.identity.domain;

import java.util.Optional;

/**
 * Outbound port for User persistence. Implemented by a JPA adapter in
 * {@code identity.infrastructure.out.persistence.write} — this interface
 * itself must never depend on Spring Data or JPA.
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
