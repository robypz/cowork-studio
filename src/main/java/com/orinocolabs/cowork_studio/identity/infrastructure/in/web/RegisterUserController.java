package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.orinocolabs.cowork_studio.identity.application.command.register.RegisterUserCommand;
import com.orinocolabs.cowork_studio.identity.application.command.register.RegisterUserCommandHandler;

import jakarta.validation.Valid;

/**
 * Inbound web adapter for exactly one use case: user registration. Each
 * command/query gets its own controller (no shared "UserController" with
 * multiple endpoints) so a controller, its request/response DTOs, and the
 * handler it calls all map 1:1. Talks only to the application layer — never
 * touches JPA, the domain repository, or any other infrastructure adapter
 * directly.
 */
@RestController
public class RegisterUserController {

    private final RegisterUserCommandHandler registerUserCommandHandler;

    public RegisterUserController(RegisterUserCommandHandler registerUserCommandHandler) {
        this.registerUserCommandHandler = registerUserCommandHandler;
    }

    @PostMapping("/api/users/register")
    public ResponseEntity<RegisterUserResponse> handle(@Valid @RequestBody RegisterUserRequest request) {
        var userId = registerUserCommandHandler.handle(
                new RegisterUserCommand(request.email(), request.password(), request.role())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(userId.value(), request.email(), request.role().toUpperCase()));
    }
}
