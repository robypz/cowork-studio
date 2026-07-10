package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Shape of the JSON body for {@code POST /api/users/register}. Validation
 * here is about "is this a well-formed request" (transport concern) — it is
 * separate from and does not replace the domain's own {@code Email}/{@code Role}
 * validation, which runs again inside {@code RegisterUserCommandHandler}.
 */
public record RegisterUserRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be a well-formed address")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters long")
        String password,

        @NotBlank(message = "role is required")
        String role
) {
}
