package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.orinocolabs.cowork_studio.identity.application.command.login.LoginCommand;
import com.orinocolabs.cowork_studio.identity.application.command.login.LoginCommandHandler;
import com.orinocolabs.cowork_studio.identity.application.command.login.LoginResult;

import jakarta.validation.Valid;

/**
 * Inbound web adapter for exactly one use case: login. Same convention as
 * {@link RegisterUserController} — one controller per command/query.
 */
@RestController
public class LoginController {

    private final LoginCommandHandler loginCommandHandler;

    public LoginController(LoginCommandHandler loginCommandHandler) {
        this.loginCommandHandler = loginCommandHandler;
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<LoginResponse> handle(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginCommandHandler.handle(new LoginCommand(request.email(), request.password()));

        return ResponseEntity.ok(
                new LoginResponse(result.accessToken(), result.userId().value(), result.role().name())
        );
    }
}
