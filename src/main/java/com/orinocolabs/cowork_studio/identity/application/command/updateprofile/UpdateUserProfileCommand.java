package com.orinocolabs.cowork_studio.identity.application.command.updateprofile;

import java.util.UUID;

/**
 * Input for updating a user's own profile. {@code userId} comes from the
 * authenticated caller, {@code newEmail} from the request body — both as
 * primitives, same convention as {@code RegisterUserCommand}.
 */
public record UpdateUserProfileCommand(UUID userId, String newEmail) {
}
