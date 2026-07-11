package com.orinocolabs.cowork_studio.identity.application.query.profile;

import java.time.Instant;
import java.util.UUID;

/**
 * Read-side shape of a user's profile. Deliberately separate from the
 * {@code User} aggregate — it is what {@link UserProfileFinder} adapters
 * produce directly from the read query, without ever materializing a domain
 * object.
 */
public record UserProfileView(UUID id, String email, String role, boolean active, Instant registeredAt) {
}
