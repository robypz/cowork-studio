package com.orinocolabs.cowork_studio.identity.testsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileFinder;
import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileView;

/**
 * In-memory stand-in for the JPA-backed read adapter, mirroring
 * {@link InMemoryUserRepository} but for the read port.
 */
public final class InMemoryUserProfileFinder implements UserProfileFinder {

    private final Map<UUID, UserProfileView> profilesById = new HashMap<>();

    public void put(UserProfileView profile) {
        profilesById.put(profile.id(), profile);
    }

    @Override
    public Optional<UserProfileView> findById(UUID userId) {
        return Optional.ofNullable(profilesById.get(userId));
    }
}
