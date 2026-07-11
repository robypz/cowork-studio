package com.orinocolabs.cowork_studio.identity.application.command.register;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.HashedPassword;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Orchestrates user registration. Deliberately a plain class — no
 * {@code @Service}, no {@code @Transactional} — so the application layer
 * stays as framework-free as the domain it wraps. It is wired as a Spring
 * bean from a {@code @Configuration} class in infrastructure, which is also
 * where transaction demarcation is decided once a use case needs it.
 */
public final class RegisterUserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserId handle(RegisterUserCommand command) {
        Email email = Email.of(command.email());
        Role role = Role.fromString(command.role());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        HashedPassword hashedPassword = passwordHasher.hash(command.rawPassword());
        User user = User.register(email, hashedPassword, role);
        userRepository.save(user);

        return user.id();
    }
}
