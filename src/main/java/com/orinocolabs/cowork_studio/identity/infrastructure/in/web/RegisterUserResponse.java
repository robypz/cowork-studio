package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import java.util.UUID;

public record RegisterUserResponse(UUID id, String email, String role) {
}
