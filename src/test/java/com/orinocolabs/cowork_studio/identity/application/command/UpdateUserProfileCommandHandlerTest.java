package com.orinocolabs.cowork_studio.identity.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.application.command.updateprofile.UpdateUserProfileCommand;
import com.orinocolabs.cowork_studio.identity.application.command.updateprofile.UpdateUserProfileCommandHandler;
import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidEmailException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserNotFoundException;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.testsupport.InMemoryUserRepository;

/**
 * Exercises the use case with an in-memory fake repository — no Spring/JPA,
 * same convention as {@link RegisterUserCommandHandlerTest}.
 */
class UpdateUserProfileCommandHandlerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final UpdateUserProfileCommandHandler handler = new UpdateUserProfileCommandHandler(userRepository);

    private User registerUser(String email) {
        User user = User.register(Email.of(email), HashedPassword.ofHash("HASHED"), Role.CLIENT);
        userRepository.save(user);
        return user;
    }

    @Test
    void updates_the_email_of_an_existing_user() {
        User user = registerUser("robert@example.com");

        handler.handle(new UpdateUserProfileCommand(user.id().value(), "new-address@example.com"));

        User updated = userRepository.findById(user.id()).orElseThrow();
        assertEquals("new-address@example.com", updated.email().value());
    }

    @Test
    void allows_keeping_the_same_email() {
        User user = registerUser("robert@example.com");

        handler.handle(new UpdateUserProfileCommand(user.id().value(), "robert@example.com"));

        User updated = userRepository.findById(user.id()).orElseThrow();
        assertEquals("robert@example.com", updated.email().value());
    }

    @Test
    void rejects_an_email_already_used_by_another_user() {
        registerUser("taken@example.com");
        User user = registerUser("robert@example.com");

        assertThrows(EmailAlreadyRegisteredException.class, () ->
                handler.handle(new UpdateUserProfileCommand(user.id().value(), "taken@example.com")));
    }

    @Test
    void rejects_a_malformed_email() {
        User user = registerUser("robert@example.com");

        assertThrows(InvalidEmailException.class, () ->
                handler.handle(new UpdateUserProfileCommand(user.id().value(), "not-an-email")));
    }

    @Test
    void rejects_an_unknown_user() {
        assertThrows(UserNotFoundException.class, () ->
                handler.handle(new UpdateUserProfileCommand(UUID.randomUUID(), "robert@example.com")));
    }
}
