package com.orinocolabs.cowork_studio.identity.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orinocolabs.cowork_studio.identity.application.command.RegisterUserCommandHandler;
import com.orinocolabs.cowork_studio.identity.domain.port.PasswordHasher;
import com.orinocolabs.cowork_studio.identity.domain.repository.UserRepository;

/**
 * Wires the identity application layer as Spring beans. This is the only
 * place that "knows" both Spring and the application/domain layers — the
 * handlers themselves stay plain Java (see {@link RegisterUserCommandHandler}).
 * Add one {@code @Bean} method here per command/query handler as they are
 * introduced.
 */
@Configuration
public class IdentityBeanConfiguration {

    @Bean
    public RegisterUserCommandHandler registerUserCommandHandler(UserRepository userRepository,
                                                                   PasswordHasher passwordHasher) {
        return new RegisterUserCommandHandler(userRepository, passwordHasher);
    }
}
