package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(UUID id, String email, String role, boolean active, Instant registeredAt) {
}
