package com.orinocolabs.cowork_studio.identity.application.query.profile;

import java.util.UUID;

/**
 * Input for reading a user's own profile. The id comes from the
 * authenticated caller (the JWT subject resolved in the web adapter), not
 * from a request body.
 */
public record GetUserProfileQuery(UUID userId) {
}
