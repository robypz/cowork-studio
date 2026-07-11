package com.orinocolabs.cowork_studio.identity.application.command;

/**
 * Input for login. Primitives, same reasoning as {@link RegisterUserCommand}:
 * this is data as it arrives from the outside world, before domain
 * validation runs.
 */
public record LoginCommand(String email, String rawPassword) {
}
