package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.write;

import java.time.Instant;
import java.util.UUID;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA record for the {@code users} table. This is the only place in the
 * identity context that is allowed to know about JPA/Hibernate — the domain
 * {@code User} aggregate stays a plain object. Mapping to/from the domain
 * type happens in {@link UserJpaMapper}, never here.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    protected UserJpaEntity() {
        // required by JPA
    }

    public UserJpaEntity(UUID id, String email, String passwordHash, Role role,
                          boolean active, Instant registeredAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
        this.registeredAt = registeredAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
