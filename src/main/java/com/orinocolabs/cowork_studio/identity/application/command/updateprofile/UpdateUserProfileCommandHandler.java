package com.orinocolabs.cowork_studio.identity.application.command.updateprofile;

import com.orinocolabs.cowork_studio.identity.domain.entity.User;
import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserNotFoundException;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Orchestrates updating a user's own profile (currently: email only).
 * Plain class, wired as a Spring bean from {@code IdentityBeanConfiguration}
 * — same convention as {@link com.orinocolabs.cowork_studio.identity.application.command.register.RegisterUserCommandHandler}.
 */
public final class UpdateUserProfileCommandHandler {

    private final UserRepository userRepository;

    public UpdateUserProfileCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handle(UpdateUserProfileCommand command) {
        UserId userId = UserId.of(command.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Email newEmail = Email.of(command.newEmail());
        if (!user.email().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyRegisteredException(newEmail);
        }

        user.changeEmail(newEmail);
        userRepository.save(user);
    }
}
