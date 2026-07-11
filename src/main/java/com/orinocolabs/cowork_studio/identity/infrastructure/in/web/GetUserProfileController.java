package com.orinocolabs.cowork_studio.identity.infrastructure.in.web;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQuery;
import com.orinocolabs.cowork_studio.identity.application.query.profile.GetUserProfileQueryHandler;
import com.orinocolabs.cowork_studio.identity.application.query.profile.UserProfileView;

/**
 * Inbound web adapter for reading the caller's own profile. {@code Principal}
 * is resolved by Spring MVC from the {@code Authentication} that
 * {@code JwtAuthenticationFilter} puts in the security context — its name is
 * the user id, set at token-parsing time.
 */
@RestController
public class GetUserProfileController {

    private final GetUserProfileQueryHandler getUserProfileQueryHandler;

    public GetUserProfileController(GetUserProfileQueryHandler getUserProfileQueryHandler) {
        this.getUserProfileQueryHandler = getUserProfileQueryHandler;
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<UserProfileResponse> handle(Principal principal) {
        UserProfileView profile = getUserProfileQueryHandler.handle(
                new GetUserProfileQuery(UUID.fromString(principal.getName()))
        );

        return ResponseEntity.ok(new UserProfileResponse(
                profile.id(), profile.email(), profile.role(), profile.active(), profile.registeredAt()
        ));
    }
}
