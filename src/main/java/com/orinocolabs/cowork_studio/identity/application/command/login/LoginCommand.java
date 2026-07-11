package com.orinocolabs.cowork_studio.identity.application.command.login;

import com.orinocolabs.cowork_studio.identity.application.command.register.RegisterUserCommand;

/**
 * Input for login. Primitives, same reasoning as {@link RegisterUserCommand}:
 * this is data as it arrives from the outside world, before domain
 * validation runs.
 */
public record LoginCommand(String email, String rawPassword) {
}
