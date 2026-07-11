package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be a well-formed address")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
}
