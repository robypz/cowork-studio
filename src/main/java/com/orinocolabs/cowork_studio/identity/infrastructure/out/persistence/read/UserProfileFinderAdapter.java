package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.read;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileFinder;
import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileView;

/**
 * Implements the read port {@link UserProfileFinder} on top of
 * {@link UserProfileJpaRepository}. This is the only class allowed to know
 * both the projection and the {@link UserProfileView} DTO shape.
 */
@Component
public class UserProfileFinderAdapter implements UserProfileFinder {

    private final UserProfileJpaRepository jpaRepository;

    public UserProfileFinderAdapter(UserProfileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserProfileView> findById(UUID userId) {
        return jpaRepository.findProjectedById(userId).map(projection -> new UserProfileView(
                projection.getId(),
                projection.getEmail(),
                projection.getRole().name(),
                projection.isActive(),
                projection.getRegisteredAt()
        ));
    }
}
