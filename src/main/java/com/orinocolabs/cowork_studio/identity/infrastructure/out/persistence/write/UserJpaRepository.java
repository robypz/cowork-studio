package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.write;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository. Package-private-ish by convention — only
 * {@link UserRepositoryAdapter} should use this directly; the rest of the
 * application talks to the domain {@code UserRepository} port instead.
 */
interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
