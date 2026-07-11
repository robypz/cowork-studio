package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Shape of the JSON body for {@code PATCH /api/users/me}. Same convention as
 * {@link RegisterUserRequest}: transport-level validation only, the domain's
 * own {@code Email} validation runs again inside
 * {@code UpdateUserProfileCommandHandler}.
 */
public record UpdateUserProfileRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be a well-formed address")
        String email
) {
}
