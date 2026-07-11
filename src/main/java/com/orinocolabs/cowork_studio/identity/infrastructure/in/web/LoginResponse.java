package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import java.util.UUID;

public record LoginResponse(String accessToken, UUID userId, String role) {
}
