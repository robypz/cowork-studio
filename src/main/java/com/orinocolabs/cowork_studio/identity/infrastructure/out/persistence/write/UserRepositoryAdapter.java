package com.orinocolabs.cowork_studio.identity.infrastructure.out.persistence.write;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Implements the domain's {@link UserRepository} port on top of Spring Data
 * JPA. This is the only class that is allowed to know both the domain model
 * and the JPA record — it exists purely to translate between the two.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        jpaRepository.save(UserJpaMapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(UserJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
