package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.write;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Translates between the {@code User} aggregate and its JPA record. Kept as
 * a small stateless mapper (not a Spring bean) so it can be unit tested
 * trivially and reused from anywhere in this adapter package.
 */
final class UserJpaMapper {

    private UserJpaMapper() {
    }

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.email().value(),
                user.password().value(),
                user.role(),
                user.isActive(),
                user.registeredAt()
        );
    }

    static User toDomain(UserJpaEntity entity) {
        return User.rehydrate(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                HashedPassword.ofHash(entity.getPasswordHash()),
                entity.getRole(),
                entity.isActive(),
                entity.getRegisteredAt()
        );
    }
}
