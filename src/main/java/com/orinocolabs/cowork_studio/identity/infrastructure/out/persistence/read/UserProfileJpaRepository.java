package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.read;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.write.UserJpaEntity;

/**
 * Read-only Spring Data repository for the profile query. Extends the
 * marker {@link Repository} (not {@code JpaRepository}) so it exposes only
 * the one finder this query needs — no {@code save}/{@code delete}, since
 * this adapter must never write. It queries the same {@code UserJpaEntity}
 * mapping the write side uses, projected into {@link UserProfileProjection}.
 *
 * <p>Deliberately named {@code findProjectedById}, not {@code findById}:
 * Spring Data JPA special-cases the literal name {@code findById} as an
 * {@code EntityManager.find()} shortcut regardless of which base interface is
 * extended, which returns the raw {@code UserJpaEntity} instead of going
 * through the interface-projection query machinery — that mismatch blows up
 * with a {@code ClassCastException} at runtime.
 */
interface UserProfileJpaRepository extends Repository<UserJpaEntity, UUID> {

    Optional<UserProfileProjection> findProjectedById(UUID id);
}
