package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.read;

import java.time.Instant;
import java.util.UUID;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;

/**
 * Spring Data closed projection over the {@code users} table. Kept in the
 * read package, separate from the write-side {@code UserJpaEntity} mapping,
 * even though today both query the same physical table — there is no
 * denormalized read model yet since this is the first read query in the
 * context.
 */
public interface UserProfileProjection {

    UUID getId();

    String getEmail();

    Role getRole();

    boolean isActive();

    Instant getRegisteredAt();
}
