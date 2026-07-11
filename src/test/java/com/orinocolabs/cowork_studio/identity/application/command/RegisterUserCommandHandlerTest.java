package com.orinocolabs.cowork_studio.identity.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidEmailException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidRoleException;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.identity.testsupport.FakePasswordHasher;
import com.orinocolabs.cowork_studio.identity.testsupport.InMemoryUserRepository;

/**
 * Exercises the use case with in-memory fakes instead of Spring/JPA/BCrypt —
 * no {@code @SpringBootTest}, no database, no application context. This is
 * the payoff of keeping domain + application framework-free: the business
 * rule ("no two users share an email") is verified in milliseconds.
 */
class RegisterUserCommandHandlerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final FakePasswordHasher passwordHasher = new FakePasswordHasher();
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
}
