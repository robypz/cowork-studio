package com.orinocolabs.cowork_studio.identity.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidCredentialsException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserDeactivatedException;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.identity.testsupport.FakePasswordHasher;
import com.orinocolabs.cowork_studio.identity.testsupport.FakeTokenIssuer;
import com.orinocolabs.cowork_studio.identity.testsupport.InMemoryUserRepository;

/**
 * Same idea as {@link RegisterUserCommandHandlerTest}: fakes only, no
 * Spring/JPA/JWT, so the login business rules are verified without any
 * framework in the loop.
 */
class LoginCommandHandlerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final FakePasswordHasher passwordHasher = new FakePasswordHasher();
    private final FakeTokenIssuer tokenIssuer = new FakeTokenIssuer();
    private final RegisterUserCommandHandler registerHandler =
            new RegisterUserCommandHandler(userRepository, passwordHasher);
    private final LoginCommandHandler loginHandler =
            new LoginCommandHandler(userRepository, passwordHasher, tokenIssuer);

    @Test
    void logs_in_with_correct_credentials_and_returns_a_token() {
        registerHandler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "CLIENT"));

        LoginResult result = loginHandler.handle(new LoginCommand("robert@example.com", "s3cret123"));

        assertEquals("CLIENT", result.role().name());
        assertFalse(result.accessToken().isBlank());
    }

    @Test
    void rejects_an_unknown_email() {
        assertThrows(InvalidCredentialsException.class, () ->
                loginHandler.handle(new LoginCommand("nobody@example.com", "whatever123")));
    }

    @Test
    void rejects_a_wrong_password() {
        registerHandler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "CLIENT"));

        assertThrows(InvalidCredentialsException.class, () ->
                loginHandler.handle(new LoginCommand("robert@example.com", "wrongPassword")));
    }

    @Test
    void rejects_login_for_a_deactivated_user() {
        UserId id = registerHandler.handle(new RegisterUserCommand("robert@example.com", "s3cret123", "CLIENT"));
        User user = userRepository.findById(id).orElseThrow();
        user.deactivate();
        userRepository.save(user);

        assertThrows(UserDeactivatedException.class, () ->
                loginHandler.handle(new LoginCommand("robert@example.com", "s3cret123")));
    }
}
