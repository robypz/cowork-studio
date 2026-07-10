package com.orinocolabs.cowork_studio.identity.domain.port;

import java.util.Optional;

import com.orinocolabs.cowork_studio.identity.domain.model.User;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

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
