package com.orinocolabs.cowork_studio.identity.application.command.register;

/**
 * Input for user registration. Deliberately made of primitives — not domain
 * value objects — since it represents data as it arrives from the outside
 * world (e.g. a JSON request body), before any domain validation has run.
 * The handler is responsible for turning these primitives into
 * {@code Email}, {@code Role}, etc.
 */
public record RegisterUserCommand(String email, String rawPassword, String role) {
}
