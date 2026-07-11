package com.orinocolabs.cowork_studio.identity.domain.entity;

import java.time.Instant;
import java.util.Objects;

import com.orinocolabs.cowork_studio.identity.domain.exception.UserAlreadyDeactivatedException;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Aggregate root of the identity bounded context. Plain Java — no JPA, no
 * Spring — so it can be created and tested without any framework on the
 * classpath. Persisted state is mapped to/from this type only inside the
 * infrastructure adapters in {@code identity.infrastructure.out.persistence}.
 */
public final class User {

    private final UserId id;
    private Email email;
    private HashedPassword password;
    private final Role role;
    private boolean active;
    private final Instant registeredAt;

    private User(UserId id, Email email, HashedPassword password, Role role,
                 boolean active, Instant registeredAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.registeredAt = registeredAt;
    }

    /**
     * Registers a brand-new user. Email uniqueness is a cross-aggregate
     * invariant — the aggregate has no way to query other users — so it must
     * be checked by the application layer (via {@code UserRepository}) before
     * calling this factory.
     */
    public static User register(Email email, HashedPassword password, Role role) {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
        Objects.requireNonNull(role, "role must not be null");
        return new User(UserId.newId(), email, password, role, true, Instant.now());
    }

    /**
     * Rehydrates a user from previously persisted state. Used exclusively by
     * the write-side persistence adapter when mapping a stored record back
     * into the domain model — never to create a new user.
     */
    public static User rehydrate(UserId id, Email email, HashedPassword password,
                                  Role role, boolean active, Instant registeredAt) {
        return new User(id, email, password, role, active, registeredAt);
    }

    public void deactivate() {
        if (!active) {
            throw new UserAlreadyDeactivatedException(id);
        }
        this.active = false;
    }

    public void changePassword(HashedPassword newPassword) {
        this.password = Objects.requireNonNull(newPassword, "password must not be null");
    }

    /**
     * Changes the user's email. Uniqueness is a cross-aggregate invariant —
     * same as in {@link #register} — so it must be checked by the
     * application layer (via {@code UserRepository}) before calling this.
     */
    public void changeEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, "email must not be null");
    }

    public UserId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public HashedPassword password() {
        return password;
    }

    public Role role() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
