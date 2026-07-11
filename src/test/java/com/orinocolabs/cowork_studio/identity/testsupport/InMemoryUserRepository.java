package com.orinocolabs.cowork_studio.identity.testsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * In-memory stand-in for the JPA-backed adapter, shared by every identity
 * test that needs a {@code UserRepository} without a real database.
 */
public final class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> usersById = new HashMap<>();

    @Override
    public void save(User user) {
        usersById.put(user.id().value(), user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(usersById.get(id.value()));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return usersById.values().stream()
                .filter(u -> u.email().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }
}
