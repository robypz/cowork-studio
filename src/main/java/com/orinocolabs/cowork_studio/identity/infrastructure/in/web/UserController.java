package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orinocolabs.cowork_studio.identity.application.command.RegisterUserCommand;
import com.orinocolabs.cowork_studio.identity.application.command.RegisterUserCommandHandler;

import jakarta.validation.Valid;

/**
 * Inbound web adapter for the identity context. Talks only to the
 * application layer (commands/handlers) — never touches JPA, the domain
 * repository, or any other infrastructure adapter directly.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterUserCommandHandler registerUserCommandHandler;

    public UserController(RegisterUserCommandHandler registerUserCommandHandler) {
        this.registerUserCommandHandler = registerUserCommandHandler;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var userId = registerUserCommandHandler.handle(
                new RegisterUserCommand(request.email(), request.password(), request.role())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(userId.value(), request.email(), request.role().toUpperCase()));
    }
}
