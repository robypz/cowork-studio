package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.orinocolabs.cowork_studio.identity.application.command.updateprofile.UpdateUserProfileCommand;
import com.orinocolabs.cowork_studio.identity.application.command.updateprofile.UpdateUserProfileCommandHandler;
import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQuery;
import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQueryHandler;
import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileView;

import jakarta.validation.Valid;

/**
 * Inbound web adapter for updating the caller's own profile. Reads the
 * updated view back through {@link GetUserProfileQueryHandler} after the
 * write, so the response always reflects what was actually persisted.
 */
@RestController
public class UpdateUserProfileController {

    private final UpdateUserProfileCommandHandler updateUserProfileCommandHandler;
    private final GetUserProfileQueryHandler getUserProfileQueryHandler;

    public UpdateUserProfileController(UpdateUserProfileCommandHandler updateUserProfileCommandHandler,
                                        GetUserProfileQueryHandler getUserProfileQueryHandler) {
        this.updateUserProfileCommandHandler = updateUserProfileCommandHandler;
        this.getUserProfileQueryHandler = getUserProfileQueryHandler;
    }

    @PatchMapping("/api/users/me")
    public ResponseEntity<UserProfileResponse> handle(Principal principal,
                                                        @Valid @RequestBody UpdateUserProfileRequest request) {
        UUID userId = UUID.fromString(principal.getName());
        updateUserProfileCommandHandler.handle(new UpdateUserProfileCommand(userId, request.email()));

        UserProfileView profile = getUserProfileQueryHandler.handle(new GetUserProfileQuery(userId));
        return ResponseEntity.ok(new UserProfileResponse(
                profile.id(), profile.email(), profile.role(), profile.active(), profile.registeredAt()
        ));
    }
}
