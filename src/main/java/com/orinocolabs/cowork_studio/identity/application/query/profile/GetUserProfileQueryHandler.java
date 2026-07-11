package com.orinocolabs.cowork_studio.identity.application.query.profile;

import com.orinocolabs.cowork_studio.identity.domain.exception.UserNotFoundException;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Orchestrates reading a user's profile. Plain class, wired as a Spring bean
 * from {@code IdentityBeanConfiguration} — same convention as the command
 * handlers. Depends only on the read port {@link UserProfileFinder}, never
 * on {@code UserRepository}.
 */
public final class GetUserProfileQueryHandler {

    private final UserProfileFinder userProfileFinder;

    public GetUserProfileQueryHandler(UserProfileFinder userProfileFinder) {
        this.userProfileFinder = userProfileFinder;
    }

    public UserProfileView handle(GetUserProfileQuery query) {
        return userProfileFinder.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(UserId.of(query.userId())));
    }
}
