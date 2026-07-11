package com.orinocolabs.cowork_studio.identity.application.command.login;

import com.orinocolabs.cowork_studio.identity.application.command.register.RegisterUserCommandHandler;
import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidCredentialsException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserDeactivatedException;
import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.port.TokenIssuer;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;

/**
 * Orchestrates login. Plain class — no {@code @Service}, no
 * {@code @Transactional} — wired as a Spring bean from
 * {@code IdentityBeanConfiguration}, same as {@link RegisterUserCommandHandler}.
 */
public final class LoginCommandHandler {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    public LoginCommandHandler(UserRepository userRepository, PasswordHasher passwordHasher,
                                TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    public LoginResult handle(LoginCommand command) {
        Email email = Email.of(command.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // Checked before the active-flag on purpose: an attacker who guesses
        // a deactivated user's email but not their password must see the
        // same generic error as any other wrong guess.
        if (!passwordHasher.matches(command.rawPassword(), user.password())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new UserDeactivatedException(user.id());
        }

        String accessToken = tokenIssuer.issueAccessToken(user.id(), user.role());
        return new LoginResult(user.id(), user.role(), accessToken);
    }
}
