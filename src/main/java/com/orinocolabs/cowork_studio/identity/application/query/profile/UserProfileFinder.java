package com.orinocolabs.cowork_studio.identity.application.query.profile;

import java.util.Optional;
import java.util.UUID;

/**
 * Read port for the user profile query. Implemented in
 * {@code identity.infrastructure.out.persistence.read} by an adapter that
 * queries the {@code users} table directly and maps straight into
 * {@link UserProfileView} — it never goes through the {@code User} aggregate
 * or the write-side {@code UserRepository}.
 */
public interface UserProfileFinder {

    Optional<UserProfileView> findById(UUID userId);
}
