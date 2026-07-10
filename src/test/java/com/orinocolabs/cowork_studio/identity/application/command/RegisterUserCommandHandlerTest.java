package com.orinocolabs.cowork_studio.identity.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidEmailException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidRoleException;
import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Exercises the use case with in-memory fakes instead of Spring/JPA/BCrypt —
 * no {@code @SpringBootTest}, no database, no application context. This is
 * the payoff of keeping domain + application framework-free: the business
 * rule ("no two users share an email") is verified in milliseconds.
 */
class RegisterUserCommandHandlerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final UppercasePasswordHasher passwordHasher = new UppercasePasswordHasher();
    private final RegisterUserCommandHandler handler =
            new RegisterUserCommandHandler(userRepository, passwordHasher);

    @Test
    void registers_a_new_user_with_hashed_password() {
        UserId id = handler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "CLIENT"));

        assertNotNull(id);
        User saved = userRepository.findById(id).orElseThrow();
        assertEquals("robert@example.com", saved.email().value());
        assertNotEquals("s3cret123", saved.password().value(), "raw password must never be stored as-is");
        assertTrue(saved.isActive());
    }

    @Test
    void rejects_a_duplicate_email() {
        handler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "CLIENT"));

        assertThrows(EmailAlreadyRegisteredException.class, () ->
                handler.handle(new RegisterUserCommand("robert@example.com", "anotherPass1", "ADMIN")));
    }

    @Test
    void rejects_a_malformed_email() {
        assertThrows(InvalidEmailException.class, () ->
                handler.handle(new RegisterUserCommand("not-an-email", "s3cret123", "CLIENT")));
    }

    @Test
    void rejects_an_unknown_role() {
        assertThrows(InvalidRoleException.class, () ->
                handler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "SUPERADMIN")));
    }

    /** Simple in-memory stand-in for the JPA-backed adapter. */
    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> usersById = new HashMap<>();

        @Override
        public void save(User user) {
            usersById.put(user.id().value(), user);
        }

        @Override
        public Optional<User> findById(UserId id) {
            return Optional.ofNullable(usersById.get(id.value()));
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return usersById.values().stream()
                    .filter(u -> u.email().equals(email))
                    .findFirst();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return findByEmail(email).isPresent();
        }
    }

    /** Deterministic stand-in for BCrypt — good enough to assert "not the raw value". */
    private static final class UppercasePasswordHasher implements PasswordHasher {
        @Override
        public HashedPassword hash(String rawPassword) {
            return HashedPassword.ofHash(rawPassword.toUpperCase());
        }

        @Override
        public boolean matches(String rawPassword, HashedPassword hashedPassword) {
            return hash(rawPassword).equals(hashedPassword);
        }
    }
}
